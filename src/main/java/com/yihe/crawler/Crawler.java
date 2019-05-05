package com.yihe.crawler;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.table.DefaultTableModel;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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

    public void crawlerKeywords(String keywords) {
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
            for (String key : keys) {
                this.crawler(key);
                this.sleep(2);
            }

            try {
                TimeUnit.SECONDS.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void crawler(String keyword) {
        try {
            driver.findElement(By.id("query")).clear();
            this.sleep(1);
            driver.findElement(By.id("query")).sendKeys(keyword);
            this.sleep(3);

            driver.findElement(By.className("swz")).click();
            this.sleep(2);
            driver.findElement(By.xpath("//*[@id=\"tool_show\"]/a")).click();
            this.sleep(1);
            driver.findElement(By.id("time")).click();
            this.sleep(1);
            driver.findElement(By.xpath("//*[@id=\"tool\"]/span[1]/div/a[2]")).click();
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
            // ex.printStackTrace();
        }
    }

    private boolean crawlerPage(String keywords) {
        this.sleep(3);

        List<WebElement> elems = this.driver.findElements(By.xpath("//*[@class=\"txt-box\"]/h3/a"));
        for (WebElement elem : elems) {
            String caption = elem.getText();
            String md5Caption = this.md5(caption);

            if (!caption.contains(keywords)) {
                return false;
            }

            boolean isExist = false;
            for (int i = 0; i < this.defaultModel.getRowCount(); i++) {
                if (this.defaultModel.getValueAt(i, 3).equals(md5Caption)) {
                    isExist = true;
                    continue;
                }
            }

            if (!isExist) {
                String count = String.valueOf(this.defaultModel.getRowCount() + 1);
                this.defaultModel.addRow(new String[] {count, "", caption, md5Caption});
            }
        }

        return true;
    }

    public void quit() {
        if (this.driver != null) {
            this.driver.quit();
        }
    }

    // 写一个md5加密的方法
    private String md5(String plainText) {
        // 定义一个字节数组
        byte[] secretBytes = null;
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 对字符串进行加密
            md.update(plainText.getBytes());
            // 获得加密后的数据
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        // 将加密后的数据转换为16进制数字
        String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }
}
