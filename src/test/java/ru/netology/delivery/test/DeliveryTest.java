package ru.netology.delivery.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;
import java.util.concurrent.locks.Condition;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.open;
import static ru.netology.delivery.data.DataGenerator.generateDate;

class DeliveryTest {

    @BeforeAll
    static void setupAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        //Configuration.holdBrowserOpen = true;
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = generateDate(daysToAddForSecondMeeting);

        $("[data-test-id='city'] .input__control").setValue(validUser.getCity());
        $("[data-test-id='date'] .input__control").click();
        $("[data-test-id='date'] .input__control").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME));
        $("[data-test-id='date'] .input__control").sendKeys(Keys.BACK_SPACE);
        $("[data-test-id='date'] .input__control").setValue(firstMeetingDate);
        $("[data-test-id='name'] .input__control").setValue(validUser.getName());
        $("[data-test-id='phone'] [name='phone']").setValue(validUser.getPhone());
        $("[data-test-id='agreement'] .checkbox__box").click();
        $(byText("Запланировать")).click();
        $(By.cssSelector("[data-test-id='success-notification'] .notification__content")).shouldHave(exactText("Встреча успешно запланирована на " + firstMeetingDate), Duration.ofMillis(15000)).shouldBe(visible);
        $("[data-test-id='date'] .input__control").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME));
        $("[data-test-id='date'] .input__control").sendKeys(Keys.BACK_SPACE);
        $("[data-test-id='date'] .input__control").setValue(secondMeetingDate);
        $(byText("Запланировать")).click();
        $(By.cssSelector("[data-test-id='replan-notification'] .notification__content")).shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"), Duration.ofMillis(15000)).shouldBe(visible);
        $(byText("Перепланировать")).click();
        $(By.cssSelector("[data-test-id='success-notification'] .notification__content")).shouldHave(exactText("Встреча успешно запланирована на " + secondMeetingDate), Duration.ofMillis(15000)).shouldBe(visible);
    }
}
