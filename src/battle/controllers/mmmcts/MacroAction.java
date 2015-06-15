package battle.controllers.mmmcts;

import asteroids.Action;

/**
 * PTSP-Competition
 * Created by Diego Perez, University of Essex.
 * Date: 16/10/12
 */
public class MacroAction
{
    public boolean m_thrust;
    public int m_steer;
    public int m_repetitions;
    public boolean m_shoot;

    public MacroAction(boolean a_t, int a_s, boolean a_sh, int a_rep)
    {
        m_thrust = a_t;
        m_steer = a_s;
        m_shoot = a_sh;
        m_repetitions = a_rep;
    }

    public MacroAction(int a_action, boolean a_sh, int a_rep)
    {
    	//System.err.println(a_action);
        m_thrust = getThrust(a_action);
        m_steer = getTurning(a_action);
        m_shoot = a_sh;
        m_repetitions = a_rep;
    }

    public Action buildAction()
    {
        return new Action(m_thrust ? 1 : 0, m_steer, m_shoot);
    }

    public static int mutateThrust(int a_action, boolean shoot)
    {
        boolean thrust = getThrust(a_action);
        int steer = getTurning(a_action);
        thrust = !thrust;
        return getActionFromInput(thrust, steer, shoot);
    }

    public static int mutateSteer(int a_action, boolean a_rightWise, boolean shoot)
    {
        boolean thrust = getThrust(a_action);
        int steer = getTurning(a_action);
        if(steer == -1 || steer == 1)
            steer = 0;
        else if(steer == 0)
        {
            if(a_rightWise)
                steer = 1;
            else
                steer = -1;
        }
        return getActionFromInput(thrust, steer, shoot);
    }

    public static boolean getThrust(int a_actionId)
    {
        if(a_actionId >= ACTION_THR_FRONT && a_actionId <= ACTION_THR_RIGHT)
            return true;

        if(a_actionId >= ACTION_THR_FRONT_SHOOT && a_actionId <= ACTION_THR_RIGHT_SHOOT)
            return true;

        return false;
    }


    /**
     * Indicates if the action given implies rotation to the left (-1), to the right (1) or no rotation at all (0).
     * @param a_actionId The identifier of the action questioned.
     * @return rotation to the left (-1), to the right (1) or no rotation at all (0).
     */
    public static int getTurning(int a_actionId)
    {
        if(a_actionId == ACTION_NO_LEFT || a_actionId == ACTION_THR_LEFT || a_actionId == ACTION_NO_LEFT_SHOOT || a_actionId == ACTION_THR_LEFT_SHOOT)
            return -1;
        else if(a_actionId == ACTION_NO_RIGHT || a_actionId == ACTION_THR_RIGHT || a_actionId == ACTION_NO_LEFT_SHOOT || a_actionId == ACTION_THR_LEFT_SHOOT)
            return 1;

        return 0; //ACTION_NO_FRONT and ACTION_THR_FRONT.
    }

    public static boolean getShoot(int a_actionId)
    {
        return a_actionId > ACTION_THR_RIGHT;
    }

    public static int getActionFromInput(boolean a_thrust, int a_turn, boolean shoot)
    {
        if(!shoot) {
            if (a_thrust) {
                if (a_turn == -1)
                    return ACTION_THR_LEFT;
                else if (a_turn == 1)
                    return ACTION_THR_RIGHT;
                else
                    return ACTION_THR_FRONT;
            } else {
                if (a_turn == -1)
                    return ACTION_NO_LEFT;
                else if (a_turn == 1)
                    return ACTION_NO_RIGHT;
                else
                    return ACTION_NO_FRONT;
            }
        } else {
            if (a_thrust) {
                if (a_turn == -1)
                    return ACTION_THR_LEFT_SHOOT;
                else if (a_turn == 1)
                    return ACTION_THR_RIGHT_SHOOT;
                else
                    return ACTION_THR_FRONT_SHOOT;
            } else {
                if (a_turn == -1)
                    return ACTION_NO_LEFT_SHOOT;
                else if (a_turn == 1)
                    return ACTION_NO_RIGHT_SHOOT;
                else
                    return ACTION_NO_FRONT_SHOOT;
            }
        }
    }

    public static final int ACTION_NO_FRONT = 0;

    public static final int ACTION_NO_LEFT = 1;

    public static final int ACTION_NO_RIGHT = 2;

    public static final int ACTION_THR_FRONT = 3;

    public static final int ACTION_THR_LEFT = 4;

    public static final int ACTION_THR_RIGHT = 5;

    public static final int ACTION_NO_FRONT_SHOOT = 6;

    public static final int ACTION_NO_LEFT_SHOOT = 7;

    public static final int ACTION_NO_RIGHT_SHOOT = 8;

    public static final int ACTION_THR_FRONT_SHOOT = 9;

    public static final int ACTION_THR_LEFT_SHOOT = 10;

    public static final int ACTION_THR_RIGHT_SHOOT = 11;

}
