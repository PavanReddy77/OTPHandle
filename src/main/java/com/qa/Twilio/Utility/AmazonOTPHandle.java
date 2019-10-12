package com.qa.Twilio.Utility;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Message;

public class AmazonOTPHandle 
{
	WebDriver driver;
	
	public static final String Account_SID = "ACe87ca340aeb4d6cc89f89b78c42f596d";
	public static final String Auth_Token = "9d159f8f49e15d217042960db320ea70";
	
	@Test
	public void otpHandle()
	{
		System.setProperty("webdriver.chrome.driver", "./Drivers/chromedriver.exe");
		driver = new ChromeDriver();
		
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			
		driver.get("https://www.amazon.in");
		
		driver.findElement(By.xpath("//a[@id='nav-link-accountList']//span[@class='nav-icon nav-arrow']")).click();
		driver.findElement(By.linkText("Start here.")).click();

		driver.findElement(By.id("ap_customer_name")).sendKeys("PavanKumar");
		
		driver.findElement(By.id("auth-country-picker-container")).click();
		driver.findElement(By.xpath("//ul[@role='application']//li/a[contains(text(),'United States +1')]")).click();
	
		driver.findElement(By.id("ap_phone_number")).sendKeys("2015033157");
		driver.findElement(By.id("ap_password")).sendKeys("Automation@1234");
		driver.findElement(By.id("continue")).click();
		
		//Get OTP using Twilio APIs
		Twilio.init(Account_SID, Auth_Token);
		String smsBody = getMessage();
		System.out.println("The SMS Body is ::: " +smsBody);
		
		String otpNumber = smsBody.replaceAll("[^-?0-9]", "");
		System.out.println("The OTP Number is ::: " +otpNumber);
		
		driver.findElement(By.id("auth-pv-enter-code")).sendKeys(otpNumber);
	}
	
	public static String getMessage() 
	{
		return getMessages().filter(m -> m.getDirection().compareTo(Message.Direction.INBOUND) == 0)
				.filter(m -> m.getTo().equals("+12015033157")).map(Message::getBody).findFirst()
				.orElseThrow(IllegalStateException::new);
	}

	private static Stream<Message> getMessages() 
	{
		ResourceSet<Message> messages = Message.reader(Account_SID).read();
		return StreamSupport.stream(messages.spliterator(), false);
	}
}
