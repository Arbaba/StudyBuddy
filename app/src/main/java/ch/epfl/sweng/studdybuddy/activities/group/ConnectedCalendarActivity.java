package ch.epfl.sweng.studdybuddy.activities.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import java.util.List;

import ch.epfl.sweng.studdybuddy.R;
import ch.epfl.sweng.studdybuddy.activities.NavigationActivity;
import ch.epfl.sweng.studdybuddy.core.Group;
import ch.epfl.sweng.studdybuddy.core.ID;
import ch.epfl.sweng.studdybuddy.core.Pair;
import ch.epfl.sweng.studdybuddy.core.User;
import ch.epfl.sweng.studdybuddy.services.calendar.Availability;
import ch.epfl.sweng.studdybuddy.services.calendar.ColorController;
import ch.epfl.sweng.studdybuddy.services.calendar.ConnectedAvailability;
import ch.epfl.sweng.studdybuddy.services.calendar.ConnectedCalendar;
import ch.epfl.sweng.studdybuddy.tools.Observable;
import ch.epfl.sweng.studdybuddy.tools.Observer;
import ch.epfl.sweng.studdybuddy.util.Messages;

/**
 * On this activity we're able as a user of the group to see
 * all availabilities of each user of the group and update our own
 * availabilities dynamically. Touching a cell of the calendar will
 * modify our availability
 */
/**
 * On this activity we're able as a user of the group to see
 * all availabilities of each user of the group and update our own
 * availabilities dynamically. Touching a cell of the calendar will
 * modify our availability
 */
public class ConnectedCalendarActivity extends AppCompatActivity implements Observer
{
    GridLayout calendarGrid;
    private static final int calendarWidth = 8;
    private float maxNumberOfUsers;
    private Availability userAvailabilities;
    private Pair pair = new Pair();

    private ConnectedCalendar calendar;

    private ColorController colorController = new ColorController();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calendar);

        calendarGrid = findViewById(R.id.calendarGrid);
        Button button = findViewById(R.id.confirmSlots);

        retrieveDataFromBundle();

        ID<User> userID = new ID<>(pair.getValue());
        ID<Group> groupID = new ID<>(pair.getKey());

        calendar = new ConnectedCalendar(this, groupID);

        try {
            userAvailabilities = ConnectedAvailability.copyExistedAvailabilities(userID, groupID);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setOnToggleBehavior(calendarGrid);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(ConnectedCalendarActivity.this, GroupActivity.class));
            }
        });

    }

    private void retrieveDataFromBundle() {
        GlobalBundle globalBundle = GlobalBundle.getInstance();
        Bundle origin = globalBundle.getSavedBundle();

        maxNumberOfUsers = (float) origin.getInt(Messages.maxUser, -1);

        pair.setKey(origin.getString(Messages.groupID));
        pair.setValue(origin.getString(Messages.userID));

        if(pair.getKey() == null || pair.getValue() == null){
            String TAG = "CALENDAR_ACTIVITY";
            Log.d(TAG, "Information of the group is not fully recovered");
            startActivity(new Intent(ConnectedCalendarActivity.this, NavigationActivity.class));
        }

    }

    /**
     * Set the behavior of every cell of the calendar so that
     * clicking on any cell will modify our availabilities in the
     * appropriate time slot
     * @param calendarGrid the View of the calendar
     */
    private void setOnToggleBehavior(GridLayout calendarGrid){
        for(int i = 0; i < calendarGrid.getChildCount(); i++)
        {
            CardView cardView = (CardView) calendarGrid.getChildAt(i);
            int column = i%calendarWidth;
            if(column!=0) {//Hours shouldn't be clickable
                int row = i/calendarWidth;
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            userAvailabilities.modifyAvailability(row, column - 1);
                        }catch(IndexOutOfBoundsException e){
                            Log.d("AVAILABILITIES", "The availabilities of the user are not correctly retrieved");
                            startActivity(new Intent(ConnectedCalendarActivity.this, NavigationActivity.class));
                        }
                    }
                });
            }
        }
    }

    /**
     * change the color of every cell of the calendar when a change has been added to
     * the availabilities of the users.
     */
    @Override
    public void update(Observable observable) {
        List<Integer> groupAvailabilities = ((ConnectedCalendar) observable).getComputedAvailabilities();
        updateColor(groupAvailabilities);
    }

    private void updateColor(List<Integer> groupAvailabilities) {
        if(groupAvailabilities.size() == ConnectedCalendar.CALENDAR_SIZE) {
            colorController.updateColor(calendarGrid, groupAvailabilities, maxNumberOfUsers, calendarWidth);
        }
    }

    public void setUserAvailabilities(Availability userAvailabilities) {
        this.userAvailabilities = userAvailabilities;
    }

    public Availability getUserAvailabilities() {
        return userAvailabilities;
    }
}
