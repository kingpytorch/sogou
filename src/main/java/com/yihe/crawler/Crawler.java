package com.yihe.crawler;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.swing.table.DefaultTableModel;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Crawler {
    /**
     * Driver
     */
    protected WebDriver driver;

    private DefaultTableModel defaultModel;

    public Crawler(DefaultTableModel defaultModel) {
        this.defaultModel = defaultModel;
    }

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 等待
     * 
     * @param seconds 等待秒数
     */
    protected void sleep(int seconds) {
        try {
            // TimeUnit.SECONDS.sleep(seconds);
            Thread.sleep(1000 * seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化驱动
     */
    protected void initDriver() {
        String chromedriver = "chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", chromedriver);

        driver = new ChromeDriver();
    }

    public void start() {
        this.initDriver();
        this.driver.get("https://weixin.sogou.com/");
        this.driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    public void crawlerKeywords() {
        String keywords = this.getKeyworkds();
        this.driver.findElement(By.xpath("//*[@id=\"pc-head\"]/div[1]/ul/li[2]/a")).click();
        this.sleep(2);

        this.driver.findElement(By.id("settings")).click();
        this.sleep(1);
        this.driver.findElement(By.id("search-settings")).click();
        this.sleep(1);
        this.driver.findElement(By.id("settings-number")).click();
        this.sleep(1);
        this.driver.findElement(By.xpath("//*[@id=\"settings-number-list\"]/li[4]/a")).click();
        this.sleep(1);
        this.driver.findElement(By.id("settings-save")).click();
        this.sleep(1);
        this.driver.navigate().back();

        String[] keys = keywords.split(",");
        for (;;) {
            try {
                for (String key : keys) {
                    this.crawler(key);
                    this.sleep(2);
                }

                TimeUnit.SECONDS.sleep(this.getSpan());
            } catch (InterruptedException e) {
                // e.printStackTrace();
            }
        }
    }

    private String getKeyworkds() {
        Properties pp = new Properties();
        try {
            pp.load(new FileInputStream("config.properties"));
        } catch (Exception ex) {

        }

        return pp.getProperty("keywords", "投票,候选,评选");
    }

    private int getSpan() {
        Properties pp = new Properties();
        try {
            pp.load(new FileInputStream("config.properties"));
        } catch (Exception ex) {

        }

        return Integer.parseInt(pp.getProperty("span", "60"));
    }

    private int getDays() {
        Properties pp = new Properties();
        try {
            pp.load(new FileInputStream("config.properties"));
        } catch (Exception ex) {

        }

        int days = Integer.parseInt(pp.getProperty("days", "1"));
        if (days > 7) {
            days = 7;
        }

        return days;
    }

    private static String getDateString(int beforeDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1 - beforeDay);

        return dateFormat.format(calendar.getTime());
    }

    public void crawler(String keyword) {
        try {
            driver.findElement(By.id("query")).clear();
            this.sleep(1);
            driver.findElement(By.id("query")).sendKeys(keyword);
            this.sleep(1);

            driver.findElement(By.className("swz")).click();
            this.sleep(1);
            driver.findElement(By.xpath("//*[@id=\"tool_show\"]/a")).click();
            this.sleep(1);
            driver.findElement(By.id("time")).click();

            this.sleep(1);
            int days = this.getDays();
            if (days > 1) {
                driver.findElement(By.id("date_start")).clear();
                driver.findElement(By.id("date_start")).sendKeys(getDateString(days));

                driver.findElement(By.id("date_end")).clear();
                driver.findElement(By.id("date_end")).sendKeys(getDateString(1));

                driver.findElement(By.id("time_enter")).click();
            } else {
                driver.findElement(By.xpath("//*[@id=\"tool\"]/span[1]/div/a[2]")).click();
            }

            this.sleep(1);

            this.crawlerPage(keyword);
            for (int i = 2; i <= 10; i++) {
                final String elementId = "sogou_page_" + i;

                WebElement ele = (new WebDriverWait(this.driver, 1)).until(new ExpectedCondition<WebElement>() {

                    @Override
                    public WebElement apply(WebDriver d) {
                        return d.findElement(By.id(elementId));
                    }
                });

                if (ele != null) {
                    ele.click();

                    if (!this.crawlerPage(keyword)) {
                        return;
                    }
                } else {
                    break;
                }
            }
        } catch (Exception ex) {
             ex.printStackTrace();
        }
    }

    private boolean crawlerPage(String keywords) {
        this.sleep(3);

        List<WebElement> elems = this.driver.findElements(By.className("txt-box"));
        for (WebElement txtBox : elems) {
            WebElement elem = txtBox.findElement(By.xpath("./h3/a"));
            String caption = elem.getText();
            if (!caption.contains(keywords)) {
                WebElement elemt = txtBox.findElement(By.className("txt-info"));
                if (elemt != null) {
                    String captiont = elemt.getText();
                    if (!captiont.contains(keywords)) {
                        return false;
                    }
                }
            }

            String md5Caption = this.md5(caption);
            boolean isExist = false;
            for (int i = 0; i < this.defaultModel.getRowCount(); i++) {
                if (this.defaultModel.getValueAt(i, 5).equals(md5Caption)) {
                    isExist = true;
                    continue;
                }
            }

            if (!isExist) {
                String count = String.valueOf(this.defaultModel.getRowCount() + 1);
                Date d = new Date();
                String dstr = dateFormat.format(d);

                this.defaultModel.addRow(new String[] {count, "", dstr, keywords, caption, md5Caption});
                ItemDao.addItem(new Item(md5Caption, caption, "", dstr, keywords, d));
            }
        }

        return true;
    }

    public void quit() {
        if (this.driver != null) {
            this.driver.quit();
        }
    }

    private String md5(String plainText) {
        byte[] secretBytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }
}
