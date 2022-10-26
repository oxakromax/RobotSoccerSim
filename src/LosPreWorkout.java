/*
 * BasicTeam.java
 */

import EDU.gatech.cc.is.abstractrobot.ControlSystemSS;
import EDU.gatech.cc.is.util.Vec2;
//Clay not used

// Trabajo de Nicolás Hidalgo, Gabriel Borges y Mathias Sonza

/**
 * Example of a simple strategy for a robot soccer team without using Clay. It
 * illustrates how to use many of the sensor and all of the motor methods of a
 * SocSmall robot.
 * <p>
 * For detailed information on how to configure behaviors, see the
 * <A HREF="../clay/docs/index.html">Clay page</A>.
 * <p>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997 Georgia Tech Research Corporation
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */
public class LosPreWorkout extends ControlSystemSS {

    /**
     * Configure the control system. This method is called once at
     * initialization time. You can use it to do whatever you like.
     */
    //[8,1][2,1][5,4][9,4][6,1]
    //[3,3,2][6,5,9][2,1,4][6,1,3][9,9,2]
    //[6,1,6][5,4,1][3,3,5][3,3,4][3,3,7]

    public double[] disPos = {0.8, 0.7, 0.7, 0.7, 0.7};
    public double[] disKick = {0.3, 0.5, 0.5, 0.4, 0.6};
    public double[] disTeam = {0.1, 0.6, 0.8, 0.2, 0.5};
    public double sum_ballx = 0;
    public long con_ballx = 0;

    public void Configure() {
        // not used in this example.
    }

    public void setParam(Integer[] disPos, Integer[] disKick, Integer[] disTeam) {
        for (int i = 0; i < 5; i++) {
            this.disPos[i] = disPos[i] / 10.0;
            this.disKick[i] = disKick[i] / 10.0;
            this.disTeam[i] = disTeam[i] / 10.0;
        }
    }

    /**
     * Called every timestep to allow the control system to run.
     */
    public int TakeStep() {
        // the eventual movement command is placed here
        Vec2 result = new Vec2(0, 0);

        // get the current time for timestamps
        long curr_time = abstract_robot.getTime();


        /*--- Get some sensor data ---*/
        // get vector to the ball
        Vec2 ball = abstract_robot.getBall(curr_time);

        // get vector to our and their goal
        Vec2 ourgoal = abstract_robot.getOurGoal(curr_time);
        Vec2 theirgoal = abstract_robot.getOpponentsGoal(curr_time);

        // get a list of the positions of our teammates
        Vec2[] teammates = abstract_robot.getTeammates(curr_time);

        /*--- check get opponents routine ---*/
        //Vec2[] opponents = abstract_robot.getOpponents(curr_time);
        //for (int i=0;i<teammates.length;i++)
        //{
        ////System.out.println(i+" team "+teammates[i].x+" "+
        //teammates[i].y+" ");
        ////System.out.println(i+" opp  "+opponents[i].x+" "+
        //opponents[i].y);
        //}
        // find the closest teammate
        Vec2 closestteammate = new Vec2(99999, 0);
        for (Vec2 teammate : teammates) {
            if (teammate.r < closestteammate.r) {
                closestteammate = teammate;
            }
        }


        /*--- now compute some strategic places to go ---*/
        // compute a point one robot radius
        // behind the ball.
        Vec2 kickspot = new Vec2(ball.x, ball.y);
        kickspot.sub(theirgoal);
        kickspot.setr(abstract_robot.RADIUS);
        kickspot.add(ball);

        // compute a point three robot radii
        // behind the ball.
        Vec2 backspot = new Vec2(ball.x, ball.y);
        backspot.sub(theirgoal);
        backspot.setr(abstract_robot.RADIUS * 5);
        backspot.add(ball);

        // compute a north and south spot
        Vec2 northspot = new Vec2(backspot.x, backspot.y + 0.7);
        Vec2 southspot = new Vec2(backspot.x, backspot.y - 0.7);

        // compute a position between the ball and defended goal
        //Vec2 goaliepos = new Vec2(ourgoal.x + ball.x, ourgoal.y + ball.y);
        Vec2 goaliepos = new Vec2(ourgoal.x, ourgoal.y + ball.y);
        goaliepos.setr(goaliepos.r * 0.5);

        // a direction away from the closest teammate.
        Vec2 awayfromclosest = new Vec2(closestteammate.x,
                closestteammate.y);
        awayfromclosest.sett(awayfromclosest.t + Math.PI);


        /*--- go to one of the places depending on player num ---*/
        int mynum = abstract_robot.getPlayerNumber(curr_time);

        sum_ballx += Math.abs((ourgoal.x - ball.x) * (ourgoal.x - ball.x));
        con_ballx += 1;
        /*--- Goalie ---*/
        double distanceTeamMatteToBall = Math.sqrt((ball.x - closestteammate.x) * (ball.x - closestteammate.x) + (ball.y - closestteammate.y) * (ball.y - closestteammate.y));
        double distanceMeToBall = Math.sqrt((ball.x - abstract_robot.getPosition(curr_time).x) * (ball.x - abstract_robot.getPosition(curr_time).x) + (ball.y - abstract_robot.getPosition(curr_time).y) * (ball.y - abstract_robot.getPosition(curr_time).y));
        if (mynum == 0) {
            // go to the goalie position if far from the ball
            if (ball.r > this.disPos[mynum]) {
                result = goaliepos;
            } // otherwise go to kick it
            else if (ball.r > this.disKick[mynum]) {
                result = kickspot;
            } else {
                result = ball;
            }
            // keep away from others
            /*if (closestteammate.r < this.disTeam[mynum])
				{
				result = awayfromclosest;
				}*/
        } /*--- midback ---*/ else if (mynum == 1) {
            // go to a midback position if far from the ball

            if (ball.r > this.disPos[mynum]) {
                result = backspot;
            } // otherwise go to kick it
            else if (ball.r > this.disKick[mynum]) {
                result = kickspot;
            } else {
                result = ball;
            }
            if (closestteammate.r < this.disTeam[mynum] && distanceTeamMatteToBall < distanceMeToBall) {
                result = awayfromclosest;
            }
            // keep away from others

        } else if (mynum == 2) {
            // go to northspot position if far from the ball
            if (ball.r > this.disPos[mynum]) {
                result = northspot;
            } // otherwise go to kick it
            else if (ball.r > this.disKick[mynum]) {
                result = kickspot;
            } else {
                result = ball;
            }
            // keep away from others
            if (closestteammate.r < this.disTeam[mynum] && distanceTeamMatteToBall < distanceMeToBall) {
                result = awayfromclosest;
            }

        } else if (mynum == 4) {
            // go to a the northspot position if far from the ball
            if (ball.r > this.disPos[mynum]) {
                result = southspot;
            } // otherwise go to kick it
            else if (ball.r > this.disKick[mynum]) {
                result = kickspot;
            } else {
                result = ball;
            }
            // keep away from others
            if (closestteammate.r < this.disTeam[mynum] && distanceTeamMatteToBall < distanceMeToBall) {
                result = awayfromclosest;
            }

        } /*---Lead Forward ---*/ else if (mynum == 3) {
            // if we are more than 4cm away from the ball
            if (ball.r > this.disPos[mynum]) // go to a good kicking position
            {
                result = kickspot;
            } else // go to the ball
            {
                result = ball;
            }
            // keep away from others
          /*  if (closestteammate.r < this.disTeam[mynum]) {
                result = awayfromclosest;
            }*/

        }


        /*--- Send commands to actuators ---*/
        // set the heading
        abstract_robot.setSteerHeading(curr_time, result.t);

        // set speed at maximum
        abstract_robot.setSpeed(curr_time, 1.0);

        // kick it if we can
        if (abstract_robot.canKick(curr_time)) {
            abstract_robot.kick(curr_time);
        }

        // tell the parent we're OK
        return (CSSTAT_OK);
    }
}
