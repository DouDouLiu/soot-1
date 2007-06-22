/* abc - The AspectBench Compiler
 * Copyright (C) 2007 Eric Bodden
 *
 * This compiler is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This compiler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this compiler, in the file LESSER-GPL;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package abc.tm.weaving.weaver.tmanalysis.ds;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import soot.Body;
import soot.jimple.Stmt;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.toolkits.scalar.Pair;

/**
 * A loop nesting tree, implemented as a tree-map.
 * Loops are represented by pairs of head-statements and the respective loop.
 * The iterator over this collection returns the loop in such an order that
 * a loop l will always returned before a loop m if l is an inner loop of m.
 *
 * @author Eric Bodden
 */
public class LoopNestTree extends TreeSet<Pair<Stmt,List<Stmt>>> {
	
	/**
	 * LoopNestTreeComparator
	 *
	 * @author Eric Bodden
	 */
	private static class LoopNestTreeComparator implements Comparator<Pair<Stmt,List<Stmt>>> {

		public int compare(Pair<Stmt, List<Stmt>> loop1, Pair<Stmt, List<Stmt>> loop2) {
			List<Stmt> stmts1 = loop1.getO2();
			List<Stmt> stmts2 = loop2.getO2();
			if(stmts1.equals(stmts2)) {
				assert loop1.getO1().equals(loop2.getO1()); //should really have the same head then
				//equal (same) loops
				return 0;
			} else if(stmts1.containsAll(stmts2)) {
				//1 superset of 2
				return 1;
			} else if(stmts1.containsAll(stmts2)) {
				//1 subset of 2
				return -1;
			} 
			//overlap (?) or disjoint: just return 0 as order does not matter
			return 0;
		}
	}

	public LoopNestTree(Body b) {
		this(computeLoops(b));		
	}

	public LoopNestTree(Map<Stmt,List<Stmt>> headToLoop) {
		super(new LoopNestTreeComparator());
		
		for (Map.Entry<Stmt,List<Stmt>> entry : headToLoop.entrySet()) {
			add(new Pair<Stmt,List<Stmt>>(entry.getKey(),entry.getValue()));
		}		
	}

	private static Map<Stmt, List<Stmt>> computeLoops(Body b) {
		LoopFinder loopFinder = new LoopFinder();
		loopFinder.transform(b);
		
		Map<Stmt, List<Stmt>> headToLoop = loopFinder.loops();
		return headToLoop;
	}
	

}