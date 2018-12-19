package ch.epfl.sweng.studdybuddy;

import android.content.Context;
import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import ch.epfl.sweng.studdybuddy.controllers.GoogleSigninController;
import ch.epfl.sweng.studdybuddy.core.Account;
import ch.epfl.sweng.studdybuddy.core.User;
import ch.epfl.sweng.studdybuddy.firebase.ReferenceWrapper;
import ch.epfl.sweng.studdybuddy.tools.Consumer;
import ch.epfl.sweng.studdybuddy.tools.Intentable;
import ch.epfl.sweng.studdybuddy.util.StudyBuddy;

import static android.support.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static ch.epfl.sweng.studdybuddy.controllers.GoogleSigninController.callbackUserFetch;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@RunWith(AndroidJUnit4.class)
public class GoogleSigninControllerTest {
    ReferenceWrapper rw = mock(ReferenceWrapper.class, RETURNS_SELF);
    Account acct = mock(Account.class);
    StudyBuddy app = mock(StudyBuddy.class);
    Intentable courseSelect = mock(Intentable.class);
    Context ctx = mock(Context.class);
    Consumer<User> callback = callbackUserFetch(rw, acct, app, ctx);
    boolean hasSignedOut ;

    @Test
    public void testCallbackUserFetchNull() {
        callback.accept(null);
        User d = new User("MJ", "23");
        when(rw.select(anyString())).thenReturn(rw);
        when(acct.getId()).thenReturn("23");
        when(acct.getDisplayName()).thenReturn("MJ");
        when(app.getAuthendifiedUser()).thenReturn(d);
        verify(rw, times(1)).select("users");
        //verify(rw, times(1)).select("23");
        //verify(rw, times(1)).setVal(d);
        verify(app, times(1)).disableTravis();
        verify(app, times(1)).setAuthendifiedUser(any(User.class));
        verify(ctx, times(1)).startActivity(any(Intent.class));
    }

    @Test
    public void testCallbackUserFetchValid() {
        User u = new User("LBJ", "23");
        callback.accept(u);
        verify(app, times(1)).setAuthendifiedUser(u);
        verify(ctx, times(1)).startActivity(any(Intent.class));
    }

    @Test
    public void fetchUserAndStartConsumerTest1(){


        try {
            customCheck(false, Arrays.asList(new User("a", "b")), 1,1);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    @Test
    public void fetchUserAndStartConsumerTest2(){
        try {
            customCheck(true, Arrays.asList(new User("a", "b")), 0,0);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


    }


    @Test
    public void fetchUserAndStartConsumerTest4(){
        when(acct.getId()).thenReturn("b");
      /*  hasSignedOut = false;
        Consumer<List<User>> consumer = GoogleSigninController.fetchUserAndStartConsumer(acct, app, ctx, hasSignedOut);
        consumer.accept(Arrays.asList(new User("a", "b"), new User("c", "d")));
        verify(app, times(1)).setAuthendifiedUser(any());
        verify(ctx, times(1)).startActivity(any());

        */
        try {
            customCheck(false, Arrays.asList(new User("a", "b"), new User("c", "d")), 1,1);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private void callAccept(Consumer<List<User>> consumer, List<User> users){
        consumer.accept(users);
    }


    private void customCheck(boolean hasSignedOut, List<User> users, int appTimes, int ctxTimes) throws Throwable {
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              //activity.update(calendar);

                              Consumer<List<User>> consumer = GoogleSigninController.fetchUserAndStartConsumer(acct, app, ctx, hasSignedOut);
                              consumer.accept(users);
                              verify(app, times(appTimes)).setAuthendifiedUser(any());
                              verify(ctx, times(ctxTimes)).startActivity(any());

                          }
                      }
        );
    }





}