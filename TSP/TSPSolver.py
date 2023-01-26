#!/usr/bin/python3

from which_pyqt import PYQT_VER
if PYQT_VER == 'PYQT5':
	from PyQt5.QtCore import QLineF, QPointF
elif PYQT_VER == 'PYQT4':
	from PyQt4.QtCore import QLineF, QPointF
else:
	raise Exception('Unsupported Version of PyQt: {}'.format(PYQT_VER))




import time
import numpy as np
from TSPClasses import *
import heapq
import itertools



class TSPSolver:
	def __init__( self, gui_view ):
		self._scenario = None

	def setupWithScenario( self, scenario ):
		self._scenario = scenario


	''' <summary>
		This is the entry point for the default solver
		which just finds a valid random tour.  Note this could be used to find your
		initial BSSF.
		</summary>
		<returns>results dictionary for GUI that contains three ints: cost of solution, 
		time spent to find solution, number of permutations tried during search, the 
		solution found, and three null values for fields not used for this 
		algorithm</returns> 
	'''

	def defaultRandomTour( self, time_allowance=60.0 ):
		results = {}
		cities = self._scenario.getCities()
		ncities = len(cities)
		foundTour = False
		count = 0
		bssf = None
		start_time = time.time()
		while not foundTour and time.time() - start_time < time_allowance:
			# create a random permutation
			perm = np.random.permutation(ncities)
			route = []
			# Now build the route using the random permutation
			for i in range(ncities):
				route.append(cities[perm[i]])
			bssf = TSPSolution(route)
			count += 1
			if bssf.cost < np.inf:
				# Found a valid route
				foundTour = True
		end_time = time.time()
		results['cost'] = bssf.cost if foundTour else math.inf
		results['time'] = end_time - start_time
		results['count'] = count
		results['soln'] = bssf
		results['max'] = None
		results['total'] = None
		results['pruned'] = None
		return results


	''' <summary>
		This is the entry point for the greedy solver, which you must implement for 
		the group project (but it is probably a good idea to just do it for the branch-and
		bound project as a way to get your feet wet).  Note this could be used to find your
		initial BSSF.
		</summary>
		<returns>results dictionary for GUI that contains three ints: cost of best solution, 
		time spent to find best solution, total number of solutions found, the best
		solution found, and three null values for fields not used for this 
		algorithm</returns> 
	'''
# this greedy algorithm has a time complexity of O(n^3) because it uses two for loops to run through all the cities
# and create a greedy path, and it will do this n times for every city it could possibly start at
# the space complexity is O(n) because all it does is create a list of all the cities in the path
	def greedy( self,time_allowance=60.0 ):
		results = {}
		cities = self._scenario.getCities()
		ncities = len(cities)
		foundTour = False
		count = 0
		bssf = None
		start_time = time.time()
		start_city = 0
		complete_path = True
		while not foundTour and time.time() - start_time < time_allowance and start_city < ncities:
			route = []
			visited = []
			previous_edge = start_city
			route.append(cities[previous_edge])
			for i in range(ncities):
				visited.append(False)
			visited[previous_edge] = True
			for i in range(ncities - 1):
				shortest_edge = np.inf
				next_edge = 0
				found_edge = False
				for j in range(ncities):
					cost = cities[previous_edge].costTo(cities[j])
					if cost < shortest_edge and not visited[j]:
						shortest_edge = cost
						next_edge = j
						found_edge = True

				if not found_edge:
					complete_path = False
				route.append(cities[next_edge])
				previous_edge = next_edge
				visited[next_edge] = True
			bssf = TSPSolution(route)
			count += 1
			if bssf.cost < np.inf and complete_path:
				# Found a valid route
				foundTour = True
			start_city += 1
		end_time = time.time()
		results['cost'] = bssf.cost if foundTour else math.inf
		results['time'] = end_time - start_time
		results['count'] = count
		results['soln'] = bssf
		results['max'] = None
		results['total'] = None
		results['pruned'] = None
		return results


	''' <summary>
		This is the entry point for the branch-and-bound algorithm that you will implement
		</summary>
		<returns>results dictionary for GUI that contains three ints: cost of best solution, 
		time spent to find best solution, total number solutions found during search (does
		not include the initial BSSF), the best solution found, and three more ints: 
		max queue size, total number of states created, and number of pruned states.</returns> 
	'''

# the time complexity of the branchAndBound is technically O(n!) however because of the pruning
# it will basically never actually run in that time so a better bound is O(b^n) where b is the
# average number of nodes put on the queue with each expansion and n is the number of cities
# the space complexity is also technically O(n!) but in reality it is also O(b^n) for the same reasons
	def branchAndBound( self, time_allowance=60.0 ):
		cities = self._scenario.getCities()
		ncities = len(cities)
		queue = []
		start_city = 0
		matrix = np.zeros((ncities, ncities))

		start_time = time.time()

		for i in range(ncities):
			for j in range(ncities):
				matrix[i, j] = cities[i].costTo(cities[j])

		bssf = self.greedy()
		if bssf['cost'] == np.inf:
			bssf = self.defaultRandomTour()
		cost = bssf['cost']
		bssf['count'] = 0
		bssf['max'] = 0
		bssf['total'] = 0
		bssf['pruned'] = 0

		matrix, bound = self.reduced_cost_matrix(matrix, ncities, 0)
		depth = 1
		queuedItem = {'depth': depth, 'bound': bound, 'matrix': matrix, 'route': np.array([])}
		heapq.heappush(queue, (bound / depth, bssf['total'], queuedItem))

		while time.time() - start_time < time_allowance and start_city < ncities:
			if len(queue) == 0:
				heapq.heappush(queue, (bound / depth, bssf['total'], queuedItem))
				start_city += 1

			if start_city == ncities:
				break

			curItem = heapq.heappop(queue)[2]

			if curItem['bound'] >= cost:
				bssf['pruned'] += 1
				continue

			if len(curItem['route']) == ncities:
				bssf['cost'] = curItem['bound']
				cost = bssf['cost']
				bssf['count'] += 1
				full_route = []
				for i in curItem['route']:
					full_route.append(cities[int(i)])
				bssf['soln'] = TSPSolution(full_route)
				continue

			if len(curItem['route']) > 0:
				previous = int(curItem['route'][len(curItem['route']) - 1])
			else:
				previous = start_city

			for i in range(ncities):
				if i == previous:
					continue

				visited = False
				for j in curItem['route']:
					if int(j) == i:
						visited = True
						break

				if visited:
					continue

				curMatrix = np.copy(curItem['matrix'])
				curMatrix, curBound = self.partial_path(curMatrix, previous, i, ncities, curItem['bound'])

				bssf['total'] += 1
				if curBound >= cost:
					bssf['pruned'] += 1
					continue

				route = curItem['route']
				route = np.append(route, i)

				newItem = {'depth': curItem['depth'] + 1, 'bound': curBound, 'matrix': curMatrix, 'route': route}
				heapq.heappush(queue, (curBound / (curItem['depth'] + 1), bssf['total'], newItem))

			if len(queue) > bssf['max']:
				bssf['max'] = len(queue)

		bssf['pruned'] += len(queue)
		end_time = time.time()
		bssf['time'] = end_time - start_time
		return bssf

# this function runs in O(n^2) time because it runs two for loops the space complexity is O(1) because it doesn't
# create any new space
	def reduced_cost_matrix(self, matrix, ncities, bound):
		curBound = bound
		for i in range(ncities):
			min_num = matrix[i, :].min()
			if min_num == np.inf:
				continue
			if min_num != 0:
				curBound += min_num
				for j in range(ncities):
					matrix[i, j] -= min_num

		for i in range(ncities):
			min_num = matrix[:, i].min()
			if min_num == np.inf:
				continue
			if min_num != 0:
				curBound += min_num
				for j in range(ncities):
					matrix[j, i] -= min_num

		return matrix, curBound

# this function runs in O(n^2) time because it has to run the reduced_cost_matrix which has that
# time complexity it also has a space complexity of O(1) because it doesn't create any new space
	def partial_path(self, matrix, row, column, ncities, bound):
		curBound = bound
		curBound += matrix[row, column]
		for i in range(ncities):
			matrix[row, i] = np.inf
			matrix[i, column] = np.inf

		matrix[column, row] = np.inf

		matrix, reduced_bound = self.reduced_cost_matrix(matrix, ncities, curBound)

		return matrix, reduced_bound



	''' <summary>
		This is the entry point for the algorithm you'll write for your group project.
		</summary>
		<returns>results dictionary for GUI that contains three ints: cost of best solution, 
		time spent to find best solution, total number of solutions found during search, the 
		best solution found.  You may use the other three field however you like.
		algorithm</returns> 
	'''

	def fancy( self,time_allowance=60.0 ):
		pass



