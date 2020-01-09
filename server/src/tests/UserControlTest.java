
import entry.*;

import static org.junit.Assert.*;

import org.junit.Test;

public class UserControlTest {

    @Test
    public void userExists() {

        UserControl userControl = new UserControl();
        assertEquals(true, userControl.userExists("myIdentifier"));
    }

}