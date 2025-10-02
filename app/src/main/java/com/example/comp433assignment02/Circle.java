package com.example.comp433assignment02;

import android.util.Log;

public class Circle {

    private float x;
    private float y;

    /**
     * Balls are bouncing straight up and down, so x values will not be changing.
     */
    float deltaY = 10;

    float originalY;

    boolean down = true;

    /**
     * Each time the ball bounces, the max will be set to 80% of the previous
     */
    float maxY;

    int numBounces = 0;

    /**
     * This helps determine when a circle reaches the ground and has "bounced," changing
     * direction and going back up 80% of the previous maxY value.
     */
    float bottomY;

    /**
     * The percentage of the new max height that circles can reach after bouncing.
     */
    float bouncePct = 0.80F;

    float radius = 15;

    public Circle(float x, float y, float radius, float bottomY, float bouncePct, int deltaY) {
        this.x = x;
        this.y = y;
        this.maxY = y;
        this.originalY = y;
        this.bottomY = bottomY;
        this.radius = radius;
        this.bouncePct = bouncePct;
        this.deltaY = deltaY;

        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Coordinates must be non-negative. Actual: ("
                    + x + ", " + y + ")");
        }

        if (radius < 0) {
            throw new IllegalArgumentException("Radius must be a positive number.");
        }

        if (bottomY < 0) {
            throw new IllegalArgumentException("bottomY must be a positive number.");
        }

        if (y > bottomY) {
            throw new IllegalArgumentException("y coordinate must be less than bottomY");
        }

        if (this.deltaY <= 0) {
            throw new IllegalArgumentException("Delta y value must be positive for the balls to move.");
        }

        if (this.bouncePct <= 0 || this.bouncePct >= 1) {
            throw new IllegalArgumentException("Bounce percentage must be greater than 0 and less than 1.");
        }
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    /**
     * Remember that (0,0) is the upper left-hand corner.
     */
    public void move() {

        // If the ball is falling...
        if (this.down) {

            // If moving the circle would put the center (Y coordinate) past the bottom (bottomY),
            // then we are officially bouncing.
            if (this.y > this.bottomY - this.radius) {

                // stop the ball eventually
                if (this.maxY >= this.bottomY - this.radius - 1) {
                    return;
                }

                // increment the number of bounces
                this.numBounces += 1;

                // change the direction
                this.down = false;

                // decrease the max height that the ball can bounce to
                float currentMaxY = this.maxY;
                this.maxY += (this.maxY * this.bouncePct);

                // reduce deltaY as well?
//                this.deltaY -= (this.deltaY * this.bouncePct);

                Log.v("move", "maxY (current): " + currentMaxY + ", maxY (new): " + this.maxY);

                // actually change the Y-coordinate and move the circle upwards
                this.y = this.y - this.deltaY;

                return;
            }

            // This means the ball can continue falling
            this.y += this.deltaY;

            return;
        }

        // To make it here means the ball is rising.
        // If moving the circle would put the center (y-coordinate) past the top (maxY),
        // then we are officially falling again
        if (this.y < this.maxY + this.deltaY + this.radius || (this.y <= 0)) {

            // change the direction; the ball is now falling
            this.down = true;

            // actually change the Y-coordinate and move the circle down
            this.y = this.y + this.deltaY;

            return;
        }

        // this means the ball can continue rising
        this.y -= this.deltaY;
    }
}
