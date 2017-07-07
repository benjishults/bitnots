package com.benjishults.bitnots.model.util

import java.util.Stack

class DirectedGraph(val V: Int) {

	var E = 0;
	val adj = Array(V) { mutableListOf<Int>() }
	val indegree = Array(V) { 0 }

	fun add(edge: Pair<Int, Int>) {
		adj[edge.first].add(edge.second)
		indegree[edge.second]++
		E++
	}

}

class DigraphWorker<T>(val G: DirectedGraph) {
	val marked: Array<Boolean> = Array(G.V) { false }
	val edgeTo: Array<Int> = Array(G.V) { 0 }
	val onStack: Array<Boolean> = Array(G.V) { false };
	var cycle: Stack<Int>? = null

	init {
		for (v in 0..G.V - 1)
			if (!marked[v] && cycle == null)
				dfs(v);
	}

	private fun dfs(v: Int) {
		onStack[v] = true;
		marked[v] = true;
		for (w in G.adj[v]) {

			// short circuit if directed cycle found
			if (cycle != null) return;

			// found new vertex, so recur
			else if (!marked[w]) {
				edgeTo[w] = v;
				dfs(w);
			}

			// trace back directed cycle
			else if (onStack[w]) {
				cycle = Stack<Int>();
				generateSequence(v) { x -> if (x == w) null else edgeTo[x] }.forEach { cycle!!.push(it) }
				cycle!!.push(w);
				cycle!!.push(v);
				if (cycle != null) {
					var first = - 1
					var last = -1
					for ( v in cycle!!) {
						if (first == -1)
							first = v;
						last = v;
					}
					if (first != last) {
						assert(false);
					}
				}
			}
		}
		onStack[v] = false;
	}

}

