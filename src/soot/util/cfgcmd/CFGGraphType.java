/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 John Jorgensen
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.util.cfgcmd;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import soot.G;
import soot.util.dot.DotGraph;
import soot.Body;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.CompleteUnitGraph;
import soot.toolkits.graph.TrapUnitGraph;
import soot.toolkits.graph.ClassicCompleteUnitGraph;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.BriefBlockGraph;
import soot.toolkits.graph.CompleteBlockGraph;
import soot.toolkits.graph.ArrayRefBlockGraph;
import soot.toolkits.graph.ZonedBlockGraph;

  /**
   * An enumeration type for representing the type of graph to
   * create, for use in tools that compare or display control	
   * flow graphs.
   *
   * 
   */
public abstract class CFGGraphType extends CFGOptionMatcher.CFGOption {

  private static final boolean DEBUG = true;  

  /**
   * <p>Interface that must be satisfied by callers of 
   * <tt>CFGGraphType.drawNodesAndEdges()</tt>.</p>
   *
   * <p>This definition is a kludge; we'd really like to just
   * use {@link CFGViewer} instead, but that would introduce
   * a circular depencency between {@link CFGViewer} and
   * {@link CFGGraphType}.</p>
   */
  public interface Viewer {
    public void drawUnexceptionalNodesAndEdges(DotGraph canvas,
					       DirectedGraph graph);
    public void drawExceptionalNodesAndEdges(DotGraph canvas,
					     CompleteUnitGraph graph);
  }

  /**
   * Method that will build a graph of this type.
   *
   * @param b The method <tt>Body</tt> from which to build the graph.
   *
   * @return The control flow graph corresponding to <tt>b</tt>
   */
  public abstract DirectedGraph buildGraph(Body b);

  /**
   * Method that will draw a {@link DotGraph} representation of the 
   * control flow in this type of graph.  This method is intended 
   * for use within {@link CFGViewer}.
   *
   * @param viewer The {@link Viewer} in whose context the
   * graph is to be drawn.
   *
   * @param canvas The {@link DotGraph} to which to add the nodes and edges.
   *
   * @param g The graph to draw.
   *
   */
  public abstract void drawNodesAndEdges(Viewer viewer, DotGraph canvas,
					 DirectedGraph g);

  private CFGGraphType(String name) {
    super(name);
  }

  /**
   * Returns the <tt>CFGGraphType</tt> identified by the
   * passed name.
   *
   * @param name A {@link String} identifying the graph type.
   *
   * @return A {@link CFGGraphType} object whose
   * {@link #drawNodesAndEdges()} method will create the desired
   * sort of control flow graph.
   */
  public static CFGGraphType getGraphType(String option) {
    return (CFGGraphType) graphTypeOptions.match(option);
  }


  /**
   * Returns a string containing the names of all the
   * available {@link CFGGraphType}s, separated by
   * '|' characters. 
   *
   * @param initialIndent The number of blank spaces to insert at the 
   *	                    beginning of the returned string. Ignored if 
   *                      negative.
   *
   * @param rightMargin   If positive, newlines will be inserted to try
   *                      to keep the length of each line in the
   *                      returned string less than or equal to
   *                      *<tt>rightMargin</tt>.
   *         
   * @param hangingIndent  If positive, this number of spaces will be
   *                       inserted immediately after each newline 
   *                       inserted to respect the <tt>rightMargin</tt>.
   */
  public static String help(int initialIndent, int rightMargin, 
			    int hangingIndent) {
    return graphTypeOptions.help(initialIndent, rightMargin, hangingIndent);
  }


  public static final CFGGraphType BRIEF_UNIT_GRAPH = 
    new CFGGraphType("BriefUnitGraph") {
      public DirectedGraph buildGraph(Body b) { 
	return new BriefUnitGraph(b); 
      }

    public void drawNodesAndEdges(Viewer viewer, DotGraph canvas,
				  DirectedGraph g) {
      viewer.drawUnexceptionalNodesAndEdges(canvas, (BriefUnitGraph) g);
    }
  };

  public static final CFGGraphType COMPLETE_UNIT_GRAPH = 
    new CFGGraphType("CompleteUnitGraph") {
    public DirectedGraph buildGraph(Body b) {
      return new CompleteUnitGraph(b);
    }
    public void drawNodesAndEdges(Viewer viewer, DotGraph canvas, 
				  DirectedGraph g) {
      viewer.drawExceptionalNodesAndEdges(canvas, (CompleteUnitGraph) g);
    }
  };

  public static final CFGGraphType TRAP_UNIT_GRAPH = 
    new CFGGraphType("TrapUnitGraph") {
    public DirectedGraph buildGraph(Body b) {
      return new TrapUnitGraph(b);
    }
    public void drawNodesAndEdges(Viewer viewer, DotGraph canvas, 
				  DirectedGraph g) {
      viewer.drawUnexceptionalNodesAndEdges(canvas, (TrapUnitGraph) g);
    }
  };

  public static final CFGGraphType CLASSIC_COMPLETE_UNIT_GRAPH = 
    new CFGGraphType("ClassicCompleteUnitGraph") {
    public DirectedGraph buildGraph(Body b) {
      return new ClassicCompleteUnitGraph(b);
    }
    public void drawNodesAndEdges(Viewer viewer, DotGraph canvas, 
				  DirectedGraph g) {
      viewer.drawUnexceptionalNodesAndEdges(canvas, 
					    (ClassicCompleteUnitGraph) g);
    }
  };

  public static final CFGGraphType BRIEF_BLOCK_GRAPH = 
    new CFGGraphType("BriefBlockGraph") {
    public DirectedGraph buildGraph(Body b) {
      return new BriefBlockGraph(b);
    }
    public void drawNodesAndEdges(Viewer viewer, DotGraph canvas, 
				  DirectedGraph g) {
      viewer.drawUnexceptionalNodesAndEdges(canvas, (BriefBlockGraph) g);
    }
  };

  public static final CFGGraphType COMPLETE_BLOCK_GRAPH = 
    new CFGGraphType("CompleteBlockGraph") {
    public DirectedGraph buildGraph(Body b) {
      return new CompleteBlockGraph(b);
    }
    public void drawNodesAndEdges(Viewer viewer, DotGraph canvas, 
				  DirectedGraph g) {
      viewer.drawUnexceptionalNodesAndEdges(canvas, (CompleteBlockGraph) g);
    }
  };

  public static final CFGGraphType ARRAY_REF_BLOCK_GRAPH = 
    new CFGGraphType("ArrayRefBlockGraph") {
    public DirectedGraph buildGraph(Body b) {
      return new ArrayRefBlockGraph(b);
    }
    public void drawNodesAndEdges(Viewer viewer, DotGraph canvas, 
				  DirectedGraph g) {
      viewer.drawUnexceptionalNodesAndEdges(canvas, (ArrayRefBlockGraph) g);
    }
  };

  public static final CFGGraphType ZONED_BLOCK_GRAPH = 
    new CFGGraphType("ZonedBlockGraph") {
    public DirectedGraph buildGraph(Body b) {
      return new ZonedBlockGraph(b);
    }
    public void drawNodesAndEdges(Viewer viewer, DotGraph canvas, 
				  DirectedGraph g) {
      viewer.drawUnexceptionalNodesAndEdges(canvas, (ZonedBlockGraph) g);
    }
  };


  private static DirectedGraph loadAltGraph(String className, Body b) {
    try {
      Class graphClass = AltClassLoader.v().loadClass(className);
      Class[] paramTypes = new Class[] { Body.class };
      Constructor constructor = graphClass.getConstructor(paramTypes);    
      DirectedGraph result = (DirectedGraph) 
	constructor.newInstance(new Object[] { b });
      return result;
    } 
    // Turn class loading exceptions into RuntimeExceptions, so callers
    // don't need to declare them:  perhaps a shoddy tactic.
    catch (ClassNotFoundException e) {
      if (DEBUG) {
	e.printStackTrace(G.v().out);
      }
      throw new IllegalArgumentException("Unable to find " + className + 
				 " in alternate classpath: " +
					 e.getMessage());
    }
    catch (NoSuchMethodException e) {
      if (DEBUG) {
	e.printStackTrace(G.v().out);
      }
      throw new IllegalArgumentException("There is no " + className + 
					 "(Body, int) constructor: " + 
					 e.getMessage());
    }
    catch (InstantiationException e) {
      if (DEBUG) {
	e.printStackTrace(G.v().out);
      }
      throw new IllegalArgumentException("Unable to instantiate " + className +
					 " in alternate classpath: " +
					 e.getMessage());
    }
    catch (IllegalAccessException e) {
      if (DEBUG) {
	e.printStackTrace(G.v().out);
      }
      throw new IllegalArgumentException("Unable to access " + className +
					 "(Body, int) in alternate classpath: " +
					 e.getMessage());
    }
    catch (InvocationTargetException e) {
      if (DEBUG) {
	e.printStackTrace(G.v().out);
      }
      throw new IllegalArgumentException("Unable to invoke " + className +
					 "(Body) in alternate classpath: " +
					 e.getMessage());
    }
  } 


  public static final CFGGraphType ALT_BRIEF_UNIT_GRAPH = 
    new CFGGraphType("AltBriefUnitGraph") {
      public DirectedGraph buildGraph(Body b) { 
	return loadAltGraph("soot.toolkits.graph.BriefUnitGraph", b);
      }

    public void drawNodesAndEdges(Viewer viewer, DotGraph canvas,
				  DirectedGraph g) {
      viewer.drawUnexceptionalNodesAndEdges(canvas, g);
    }
  };

  public static final CFGGraphType ALT_COMPLETE_UNIT_GRAPH = 
    new CFGGraphType("AltCompleteUnitGraph") {
      public DirectedGraph buildGraph(Body b) { 
	return loadAltGraph("soot.toolkits.graph.CompleteUnitGraph", b);
      }

    public void drawNodesAndEdges(Viewer viewer, DotGraph canvas,
				  DirectedGraph g) {
      viewer.drawUnexceptionalNodesAndEdges(canvas, g);
    }
  };

  public static final CFGGraphType ALT_TRAP_UNIT_GRAPH = 
    new CFGGraphType("AltTrapUnitGraph") {
      public DirectedGraph buildGraph(Body b) { 
	return loadAltGraph("soot.toolkits.graph.TrapUnitGraph", b);
      }

    public void drawNodesAndEdges(Viewer viewer, DotGraph canvas,
				  DirectedGraph g) {
      viewer.drawUnexceptionalNodesAndEdges(canvas, g);
    }
  };

  public static final CFGGraphType ALT_ARRAY_REF_BLOCK_GRAPH = 
    new CFGGraphType("AltArrayRefBlockGraph") {
      public DirectedGraph buildGraph(Body b) { 
	return loadAltGraph("soot.toolkits.graph.ArrayRefBlockGraph", b);
      }

    public void drawNodesAndEdges(Viewer viewer, DotGraph canvas,
				  DirectedGraph g) {
      viewer.drawUnexceptionalNodesAndEdges(canvas, g);
    }
  };

  public static final CFGGraphType ALT_BRIEF_BLOCK_GRAPH = 
    new CFGGraphType("AltBriefBlockGraph") {
      public DirectedGraph buildGraph(Body b) { 
	return loadAltGraph("soot.toolkits.graph.BriefBlockGraph", b);
      }

    public void drawNodesAndEdges(Viewer viewer, DotGraph canvas,
				  DirectedGraph g) {
      viewer.drawUnexceptionalNodesAndEdges(canvas, g);
    }
  };

  public static final CFGGraphType ALT_COMPLETE_BLOCK_GRAPH = 
    new CFGGraphType("AltCompleteBlockGraph") {
      public DirectedGraph buildGraph(Body b) { 
	return loadAltGraph("soot.toolkits.graph.CompleteBlockGraph", b);
      }

    public void drawNodesAndEdges(Viewer viewer, DotGraph canvas,
				  DirectedGraph g) {
      viewer.drawUnexceptionalNodesAndEdges(canvas, g);
    }
  };

  public static final CFGGraphType ALT_ZONED_BLOCK_GRAPH = 
    new CFGGraphType("AltZonedBlockGraph") {
      public DirectedGraph buildGraph(Body b) { 
	return loadAltGraph("soot.toolkits.graph.ZonedBlockGraph", b);
      }

    public void drawNodesAndEdges(Viewer viewer, DotGraph canvas,
				  DirectedGraph g) {
      viewer.drawUnexceptionalNodesAndEdges(canvas, g);
    }
  };

  private final static CFGOptionMatcher graphTypeOptions = 
    new CFGOptionMatcher(new CFGGraphType[] {    
      BRIEF_UNIT_GRAPH,
      COMPLETE_UNIT_GRAPH,
      TRAP_UNIT_GRAPH,
      CLASSIC_COMPLETE_UNIT_GRAPH,
      BRIEF_BLOCK_GRAPH,
      COMPLETE_BLOCK_GRAPH,
      ARRAY_REF_BLOCK_GRAPH,
      ZONED_BLOCK_GRAPH,
      ALT_ARRAY_REF_BLOCK_GRAPH,
      ALT_BRIEF_UNIT_GRAPH,
      ALT_COMPLETE_UNIT_GRAPH,
      ALT_TRAP_UNIT_GRAPH,
      ALT_BRIEF_BLOCK_GRAPH,
      ALT_COMPLETE_BLOCK_GRAPH,
      ALT_ZONED_BLOCK_GRAPH,
    });
}
