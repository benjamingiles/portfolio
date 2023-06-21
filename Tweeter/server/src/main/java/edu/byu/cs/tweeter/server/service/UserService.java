package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutReponse;
import edu.byu.cs.tweeter.server.dao.AuthtokenDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamodb.bean.UserBean;
import edu.byu.cs.tweeter.server.factory.Factory;

public class UserService {

    private Factory factory;
    private UserDAO userDAO;
    private AuthtokenDAO authtokenDAO;

    public UserService(Factory factory) {
        this.factory = factory;
        userDAO = factory.getUserDAO();
        authtokenDAO = factory.getAuthtokenDAO();
    }

    public AuthenticateResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        UserBean userBean = userDAO.getUser(request.getUsername());

        if(userBean == null) {
            return new AuthenticateResponse("Invalid Username");
        }


        try {
            if (validatePassword(request.getPassword(), userBean.getPassword())) {
                User user = new User(userBean.getFirst_name(), userBean.getLast_name(),
                        userBean.getAlias(), userBean.getImageURL());

                AuthToken authToken = generateAuthToken(request.getUsername());
                return new AuthenticateResponse(user, authToken);
            } else {
                return new AuthenticateResponse("Incorrect Password");
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public AuthenticateResponse register(RegisterRequest request) {
        if (request.getUsername() == null) {
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if (request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        } else if (request.getFirstName() == null) {
            throw new RuntimeException("[Bad Request] Missing a first name");
        } else if (request.getLastName() == null) {
            throw new RuntimeException("[Bad Request] Missing a last name");
        } else if (request.getImage() == null) {
            throw new RuntimeException("[Bad Request] Missing an image");
        }

        UserBean userBean = userDAO.getUser(request.getUsername());

        if (userBean == null) {
            String imageLink = uploadImage(request.getImage(), request.getUsername());
            User user = new User(request.getFirstName(), request.getLastName(),
                    request.getUsername(), imageLink);

            String hashPass = null;
            try {
                hashPass = generateStrongPasswordHash(request.getPassword());
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }

            userDAO.putUser(request.getUsername(), request.getFirstName(), request.getLastName(),
                    imageLink, 0, 0, hashPass);

            AuthToken authToken = generateAuthToken(request.getUsername());
            return new AuthenticateResponse(user, authToken);
        } else {
            return new AuthenticateResponse("Username Taken");
        }
    }

    public LogoutReponse logout(LogoutRequest request) {
        if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] missing an authToken");
        }

        authtokenDAO.deleteAuthtoken(request.getAuthToken().getToken());
        return new LogoutReponse();
    }

    public GetUserResponse getUser(GetUserRequest request) {
        if (request.getAlias() == null) {
            throw new RuntimeException("[Bad Request] missing an alias");
        }

        UserBean userBean = userDAO.getUser(request.getAlias());
        User user = new User(userBean.getFirst_name(), userBean.getLast_name(),
                userBean.getAlias(), userBean.getImageURL());
        return new GetUserResponse(user);
    }

    private AuthToken generateAuthToken(String alias) {
        UUID uuid = UUID.randomUUID();
        String token = uuid.toString();
        long timestamp = System.currentTimeMillis();
        String datetime = String.valueOf(timestamp);

        authtokenDAO.putAuthtoken(token, alias, timestamp);

        return new AuthToken(token, datetime);
    }

    private boolean validatePassword(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ testHash.length;
        for(int i = 0; i < hash.length && i < testHash.length; i++)
        {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }
    private byte[] fromHex(String hex) throws NoSuchAlgorithmException
    {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
        {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    private String generateStrongPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private String toHex(byte[] array) throws NoSuchAlgorithmException
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
        {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }

    private String uploadImage(String image_string, String alias) {
        AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withRegion("us-west-2")
                .build();

        byte[] byteArray = Base64.getDecoder().decode(image_string);

        ObjectMetadata data = new ObjectMetadata();

        data.setContentLength(byteArray.length);

        data.setContentType("image/jpeg");

        PutObjectRequest request = new PutObjectRequest("benbucket890", alias,
                new ByteArrayInputStream(byteArray), data).withCannedAcl(CannedAccessControlList.PublicRead);

        s3.putObject(request);

        return "https://benbucket890.s3.us-west-2.amazonaws.com/" + alias;
    }
}
