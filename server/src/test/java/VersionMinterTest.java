import com.netflix.vms.transformer.util.VersionMinter;
import org.junit.Test;
import org.testng.Assert;

public class VersionMinterTest {
    
    @Test
    public void has17Digits() {
        VersionMinter minter = new VersionMinter();
        
        long version = minter.mintANewVersion();
        
        Assert.assertEquals(17, String.valueOf(version).length());
    }
    
    @Test
    public void lastThreeDigitsIncrementUntilRollingOver() {
        VersionMinter minter = new VersionMinter();
        
        for(int i=0;i<4000;i++) {
            long newVersion = minter.mintANewVersion();
            long versionCounter = newVersion % 1000;
            
            Assert.assertEquals((i+1)%1000, versionCounter);
        }
        
    }

}
