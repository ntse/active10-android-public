package com.flipsidegroup.active10.utils.curvedpath


/**
 * A class that holds information about a location and how the path should get to that
 * location from the previous path location (if any). Any PathPoint holds the information for
 * its location as well as the instructions on how to traverse the preceding interval from the
 * previous location.
 */


/**
 * The possible path operations that describe how to move from a preceding PathPoint to the
 * location described by this PathPoint.
 */

const val MOVE = 0
const val LINE = 1
const val CURVE = 2

class PathPoint {

    /**
     * The location of this PathPoint
     */
    internal var mX = 0f
    internal var mY = 0f

    /**
     * The first control point, if any, for a PathPoint of type CURVE
     */
    internal var mControl0X = 0f
    internal var mControl0Y = 0f

    /**
     * The second control point, if any, for a PathPoint of type CURVE
     */
    internal var mControl1X = 0f
    internal var mControl1Y = 0f

    /**
     * The motion described by the path to get from the previous PathPoint in an AnimatorPath
     * to the location of this PathPoint. This can be one of MOVE, LINE, or CURVE.
     */
    internal var mOperation = 0

    /**
     * Line/Move constructor
     */
    private constructor(operation: Int, x: Float, y: Float) {
        mOperation = operation
        mX = x
        mY = y
    }

    /**
     * Curve constructor
     */
    constructor(c0X: Float, c0Y: Float, c1X: Float, c1Y: Float, x: Float, y: Float) {
        mControl0X = c0X
        mControl0Y = c0Y
        mControl1X = c1X
        mControl1Y = c1Y
        mX = x
        mY = y
        mOperation = CURVE
    }

    companion object {
        /**
         * Constructs and returns a PathPoint object that describes a line to the given xy location.
         */
        fun lineTo(x: Float, y: Float): PathPoint {
            return PathPoint(LINE, x, y)
        }

        /**
         * Constructs and returns a PathPoint object that describes a curve to the given xy location
         * with the control points at c0 and c1.
         */
        fun curveTo(c0X: Float, c0Y: Float, c1X: Float, c1Y: Float, x: Float, y: Float): PathPoint {
            return PathPoint(c0X, c0Y, c1X, c1Y, x, y)
        }

        /**
         * Constructs and returns a PathPoint object that describes a discontinuous move to the given
         * xy location.
         */
        fun moveTo(x: Float, y: Float): PathPoint {
            return PathPoint(MOVE, x, y)
        }
    }
}