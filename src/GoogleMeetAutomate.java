import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GoogleMeetAutomate {
static WebDriver webDriver;
static String password="";
static String emailId="";
static String meetId="";
static String classTiming="";
static int thresholdLimit=45;   //usually it should be 45 to 50
static int leaveLimit=20;   //usually 10 to 15
static boolean classBegun=false;
static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
static Scanner sc=new Scanner(System.in);
    public static void main(String[] args) throws InterruptedException, ParseException {
        inputFeed();
        System.out.println("Process initiated, waiting for the time...");
        Timer t=new Timer();

        //Creating a schedule to join the meeting at the specified time
        t.schedule(new TimerTask() {
            public void run() {
                System.out.println("The time has come... starting the task");
                InitializeChrome();
                try {
                    GoogleAuthentication();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    JoinGoogleMeet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    LeaveMeetLoop();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                webDriver.close();
            }
        }, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(classTiming));

}
    public static void InitializeChrome() { ChromeOptions options = new ChromeOptions();
        options.addArguments("use-fake-device-for-media-stream");
        options.addArguments("use-fake-ui-for-media-stream");

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browser", "Chrome");
        caps.setCapability("browser_version", "75.0");
        caps.setCapability("os", "Windows");
        caps.setCapability("os_version", "10");
        caps.setCapability(ChromeOptions.CAPABILITY, options);
        Map<String, Object> prefs = new HashMap<String, Object>();
        Map<String, Object> profile = new HashMap<String, Object>();
        Map<String, Object> contentSettings = new HashMap<String, Object>();
        contentSettings.put("notifications", 2);
        profile.put("managed_default_content_settings", contentSettings);
        prefs.put("profile", profile);
        options.setExperimentalOption("prefs", prefs);

        // SET CAPABILITY
        caps.setCapability(ChromeOptions.CAPABILITY, options);

        System.setProperty("webdriver.chrome.driver","D://as//SeleniumJars and exe//drivers//chromedriver.exe");
        webDriver= new ChromeDriver(caps);

    }
    public static void GoogleAuthentication() throws InterruptedException {
        //Login to Google account
        webDriver.get("https://accounts.google.com/o/oauth2/v2/auth/oauthchooseaccount?redirect_uri=https%3A%2F%2Fdevelopers.google.com%2Foauthplayground&prompt=consent&response_type=code&client_id=407408718192.apps.googleusercontent.com&scope=email&access_type=offline&flowName=GeneralOAuthFlow");
        WebElement input = webDriver.findElement(By.xpath("//input[@type=\"email\"]"));
        input.clear();
        input.click();
        Thread.sleep(500);
        input.sendKeys(emailId);
        input.sendKeys(Keys.ENTER);
        // webDriver.wait(150);
        Thread.sleep(3000);

        WebElement pass= webDriver.findElement(By.xpath("//input[@type=\"password\"]"));

        pass.click();
        pass.sendKeys(password);
        Thread.sleep(500);
        pass.sendKeys(Keys.ENTER);
        Thread.sleep(2500);
    }
    public static void JoinGoogleMeet() throws InterruptedException {
        Thread.sleep(500);

        webDriver.get(meetId);
       // webDriver.get(meetId);
        Thread.sleep(6000);
        //turn off mic
        webDriver.findElement(By.xpath("//*[@id=\"yDmH0d\"]/c-wiz/div/div/div[8]/div[3]/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div[4]/div[1]/div/div/div")).click();


        //turn off cam
        webDriver.findElement(By.xpath("//*[@id=\"yDmH0d\"]/c-wiz/div/div/div[8]/div[3]/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div[4]/div[2]/div/div")).click();
        Thread.sleep(2000);
        WebElement joinMeet= webDriver.findElement(By.xpath("//*[@id=\"yDmH0d\"]/c-wiz/div/div/div[8]/div[3]/div/div/div[2]/div/div[1]/div[2]/div/div[2]/div/div[1]/div[1]/span/span"));
        joinMeet.click();
        LocalDateTime now = LocalDateTime.now();
        System.out.println("Joined the meeting at: "+dtf.format(now));
        Thread.sleep(120000);
    }
    public static void LeaveMeetLoop() throws InterruptedException {
        int people_number=0;
        while(true){
            Thread.sleep(60000);
            try {
                people_number = Integer.parseInt(webDriver.findElement(By.xpath("//*[contains(@class,'wnPUne N0PJ8e')]")).getText());
            }
             catch(Exception exception1){
            try
            {people_number =Integer.parseInt(webDriver.findElement(By.className("rua5Nb")).getText());}
          catch(Exception exception2){continue;}
            }
            System.out.println("Current members: "+people_number);
            if(!classBegun&&people_number>=thresholdLimit) {classBegun=true;
                System.out.println("The class has begun");}
            if(classBegun&&people_number<=leaveLimit) break;

        }
    System.out.println("Okay nigga, that was a really boring class xD");
        //Don't call me racist, that was just a random statement!
quitMeet();
Thread.sleep(2000);



    }
    public static void quitMeet(){
        LocalDateTime now = LocalDateTime.now();
        System.out.println("Left the meeting at: "+dtf.format(now));
       try{ webDriver.findElement(By.xpath("//*[@id=\"ow3\"]/div[1]/div/div[8]/div[3]/div[9]/div[2]/div[2]/div")).click();}
       catch(Exception cantQuit){
           webDriver.quit();
       }
    }
    public static void inputFeed(){
        System.out.println("Enter your Email ID");
        emailId=sc.nextLine().trim();
        System.out.println("Enter Your Password");
        password=sc.nextLine().trim();
        System.out.println("Enter meeting URL");
        meetId=sc.nextLine().trim();
        System.out.println("Specify class timing (HH:MM)");
        String tempclassTiming=sc.nextLine().trim();
        LocalDateTime now = LocalDateTime.now();
        System.out.println(dtf.format(now));
        classTiming=dtf.format(now).substring(0,4)+"-"+dtf.format(now).substring(5,7)+"-"+dtf.format(now).substring(8,11)+tempclassTiming+":05";
    }
}