import org.hibernate.annotations.common.Version;
import com.ty.test.MyUser

public class TestDown {
    public static void main(String[] args) {
        Version version = new Version();
        System.out.println(version.toString());
        MyUser user = new MyUser();
        System.out.println(user.toString());
    }
}
