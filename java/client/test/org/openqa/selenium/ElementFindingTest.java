/*
Copyright 2012 Software Freedom Conservancy
Copyright 2007-2012 Selenium committers

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package org.openqa.selenium;

import org.junit.Test;
import org.openqa.selenium.testing.Ignore;
import org.openqa.selenium.testing.JUnit4TestBase;
import org.openqa.selenium.testing.JavascriptEnabled;
import org.openqa.selenium.testing.TestUtilities;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeFalse;
import static org.openqa.selenium.testing.Ignore.Driver.ANDROID;
import static org.openqa.selenium.testing.Ignore.Driver.IE;
import static org.openqa.selenium.testing.Ignore.Driver.IPHONE;
import static org.openqa.selenium.testing.Ignore.Driver.MARIONETTE;
import static org.openqa.selenium.testing.Ignore.Driver.OPERA;
import static org.openqa.selenium.testing.Ignore.Driver.OPERA_MOBILE;
import static org.openqa.selenium.testing.Ignore.Driver.REMOTE;
import static org.openqa.selenium.testing.TestUtilities.isOldIe;
import static org.openqa.selenium.TestWaiter.waitFor;
import static org.openqa.selenium.WaitingConditions.pageTitleToBe;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.List;

public class ElementFindingTest extends JUnit4TestBase {

  @Test
  public void testShouldNotBeAbleToLocateASingleElementThatDoesNotExist() {
    driver.get(pages.formPage);

    try {
      driver.findElement(By.id("nonExistantButton"));
      fail("Should not have succeeded");
    } catch (NoSuchElementException e) {
      // this is expected
    }
  }

  @Test
  public void testShouldBeAbleToClickOnLinkIdentifiedByText() {
    driver.get(pages.xhtmlTestPage);
    driver.findElement(By.linkText("click me")).click();

    waitFor(pageTitleToBe(driver, "We Arrive Here"));

    assertThat(driver.getTitle(), equalTo("We Arrive Here"));
  }

  @Test
  public void testDriverShouldBeAbleToFindElementsAfterLoadingMoreThanOnePageAtATime() {
    driver.get(pages.formPage);
    driver.get(pages.xhtmlTestPage);
    driver.findElement(By.linkText("click me")).click();

    waitFor(pageTitleToBe(driver, "We Arrive Here"));

    assertThat(driver.getTitle(), equalTo("We Arrive Here"));
  }

  @Test
  public void testShouldBeAbleToClickOnLinkIdentifiedById() {
    driver.get(pages.xhtmlTestPage);
    driver.findElement(By.id("linkId")).click();

    waitFor(pageTitleToBe(driver, "We Arrive Here"));

    assertThat(driver.getTitle(), equalTo("We Arrive Here"));
  }

  @Test
  public void testShouldThrowAnExceptionWhenThereIsNoLinkToClickAndItIsFoundWithLinkText() {
    driver.get(pages.xhtmlTestPage);

    try {
      driver.findElement(By.linkText("Not here either"));
      fail("Should not have succeeded");
    } catch (NoSuchElementException e) {
      // this is expected
    }
  }

  @Test
  public void testShouldFindAnElementBasedOnId() {
    driver.get(pages.formPage);

    WebElement element = driver.findElement(By.id("checky"));

    assertThat(element.isSelected(), is(false));
  }

  @Test
  public void testShouldNotBeAbleToFindElementsBasedOnIdIfTheElementIsNotThere() {
    driver.get(pages.formPage);

    try {
      driver.findElement(By.id("notThere"));
      fail("Should not have succeeded");
    } catch (NoSuchElementException e) {
      // this is expected
    }
  }

  @Test
  public void testShouldFindElementsByName() {
    driver.get(pages.formPage);

    WebElement element = driver.findElement(By.name("checky"));

    assertThat(element.getAttribute("value"), is("furrfu"));
  }

  @Test
  public void testShouldFindElementsByClass() {
    driver.get(pages.xhtmlTestPage);

    WebElement element = driver.findElement(By.className("extraDiv"));
    assertTrue(element.getText().startsWith("Another div starts here."));
  }

  @Test
  public void testShouldFindElementsByClassWhenItIsTheFirstNameAmongMany() {
    driver.get(pages.xhtmlTestPage);

    WebElement element = driver.findElement(By.className("nameA"));
    assertThat(element.getText(), equalTo("An H2 title"));
  }

  @Test
  public void testShouldFindElementsByClassWhenItIsTheLastNameAmongMany() {
    driver.get(pages.xhtmlTestPage);

    WebElement element = driver.findElement(By.className("nameC"));
    assertThat(element.getText(), equalTo("An H2 title"));
  }

  @Test
  public void testShouldFindElementsByClassWhenItIsInTheMiddleAmongMany() {
    driver.get(pages.xhtmlTestPage);

    WebElement element = driver.findElement(By.className("nameBnoise"));
    assertThat(element.getText(), equalTo("An H2 title"));
  }

  @Test
  public void testShouldFindElementByClassWhenItsNameIsSurroundedByWhitespace() {
    driver.get(pages.xhtmlTestPage);

    WebElement element = driver.findElement(By.className("spaceAround"));
    assertThat(element.getText(), equalTo("Spaced out"));
  }

  @Test
  public void testShouldFindElementsByClassWhenItsNameIsSurroundedByWhitespace() {
    driver.get(pages.xhtmlTestPage);

    List<WebElement> elements = driver.findElements(By.className("spaceAround"));
    assertThat(elements.size(), equalTo(1));
    assertThat(elements.get(0).getText(), equalTo("Spaced out"));
  }

  @Test
  public void testShouldNotFindElementsByClassWhenTheNameQueriedIsShorterThanCandidateName() {
    driver.get(pages.xhtmlTestPage);

    try {
      driver.findElement(By.className("nameB"));
      fail("Should not have succeeded");
    } catch (NoSuchElementException e) {
      // this is expected
    }
  }

  @Test
  public void testShouldBeAbleToFindMultipleElementsByXPath() {
    driver.get(pages.xhtmlTestPage);

    List<WebElement> elements = driver.findElements(By.xpath("//div"));

    assertTrue(elements.size() > 1);
  }

  @Test
  public void testShouldBeAbleToFindMultipleElementsByLinkText() {
    driver.get(pages.xhtmlTestPage);

    List<WebElement> elements = driver.findElements(By.linkText("click me"));

    assertTrue("Expected 2 links, got " + elements.size(), elements.size() == 2);
  }

  @Test
  public void testShouldBeAbleToFindMultipleElementsByPartialLinkText() {
    driver.get(pages.xhtmlTestPage);

    List<WebElement> elements = driver.findElements(By.partialLinkText("ick me"));

    assertTrue(elements.size() == 2);
  }

  @Test
  public void testShouldBeAbleToFindElementByPartialLinkText() {
    driver.get(pages.xhtmlTestPage);

    try {
      driver.findElement(By.partialLinkText("anon"));
    } catch (NoSuchElementException e) {
      fail("Expected element to be found");
    }
  }

  @Test
  public void testShouldFindElementByLinkTextContainingEqualsSign() {
    driver.get(pages.xhtmlTestPage);

    try {
      WebElement element = driver.findElement(By.linkText("Link=equalssign"));
      assertEquals("linkWithEqualsSign", element.getAttribute("id"));
    } catch (NoSuchElementException e) {
      fail("Expected element to be found");
    }
  }

  @Test
  public void testShouldFindElementByPartialLinkTextContainingEqualsSign() {
    driver.get(pages.xhtmlTestPage);

    try {
      WebElement element = driver.findElement(By.partialLinkText("Link="));
      assertEquals("linkWithEqualsSign", element.getAttribute("id"));
    } catch (NoSuchElementException e) {
      fail("Expected element to be found");
    }
  }

  @Test
  public void testShouldFindElementsByLinkTextContainingEqualsSign() {
    driver.get(pages.xhtmlTestPage);

    List<WebElement> elements = driver.findElements(By.linkText("Link=equalssign"));
    assertEquals(1, elements.size());
    assertEquals("linkWithEqualsSign", elements.get(0).getAttribute("id"));
  }

  @Test
  public void testShouldFindElementsByPartialLinkTextContainingEqualsSign() {
    driver.get(pages.xhtmlTestPage);

    List<WebElement> elements = driver.findElements(By.partialLinkText("Link="));
    assertEquals(1, elements.size());
    assertEquals("linkWithEqualsSign", elements.get(0).getAttribute("id"));
  }

  @Test
  public void testShouldBeAbleToFindMultipleElementsByName() {
    driver.get(pages.nestedPage);

    List<WebElement> elements = driver.findElements(By.name("checky"));

    assertTrue(elements.size() > 1);
  }

  @Ignore(value = ANDROID, reason = "Bug in Android's XPath library.")
  @Test
  public void testShouldBeAbleToFindMultipleElementsById() {
    driver.get(pages.nestedPage);

    List<WebElement> elements = driver.findElements(By.id("2"));

    assertEquals(8, elements.size());
  }

  @Test
  public void testShouldBeAbleToFindMultipleElementsByClassName() {
    driver.get(pages.xhtmlTestPage);

    List<WebElement> elements = driver.findElements(By.className("nameC"));

    assertTrue(elements.size() > 1);
  }

  // You don't want to ask why this is here
  @Test
  public void testWhenFindingByNameShouldNotReturnById() {
    driver.get(pages.formPage);

    WebElement element = driver.findElement(By.name("id-name1"));
    assertThat(element.getAttribute("value"), is("name"));

    element = driver.findElement(By.id("id-name1"));
    assertThat(element.getAttribute("value"), is("id"));

    element = driver.findElement(By.name("id-name2"));
    assertThat(element.getAttribute("value"), is("name"));

    element = driver.findElement(By.id("id-name2"));
    assertThat(element.getAttribute("value"), is("id"));
  }

  @Test
  public void testShouldReturnElementsThatDoNotSupportTheNameProperty() {
    driver.get(pages.nestedPage);

    driver.findElement(By.name("div1"));
    // If this works, we're all good
  }

  @Test
  public void testShouldFindHiddenElementsByName() {
    driver.get(pages.formPage);

    try {
      driver.findElement(By.name("hidden"));
    } catch (NoSuchElementException e) {
      fail("Expected to be able to find hidden element");
    }
  }

  @Test
  public void testShouldFindAnElementBasedOnTagName() {
    driver.get(pages.formPage);

    WebElement element = driver.findElement(By.tagName("input"));

    assertNotNull(element);
  }

  @Test
  public void testShouldFindElementsBasedOnTagName() {
    driver.get(pages.formPage);

    List<WebElement> elements = driver.findElements(By.tagName("input"));

    assertNotNull(elements);
  }

  @Test
  public void testFindingByCompoundClassNameIsAnError() {
    driver.get(pages.xhtmlTestPage);

    try {
      driver.findElement(By.className("a b"));
      fail("Compound class names aren't allowed");
    } catch (InvalidSelectorException e) {
      // This is expected
    }

    try {
      driver.findElements(By.className("a b"));
      fail("Compound class names aren't allowed");
    } catch (InvalidSelectorException e) {
      // This is expected
    }
  }

  @Test
  public void testShouldNotBeAbleToFindAnElementOnABlankPage() {
    driver.get("about:blank");

    try {
      // Search for anything. This used to cause an IllegalStateException in IE.
      driver.findElement(By.tagName("a"));
      fail("Should not have been able to find a link");
    } catch (NoSuchElementException e) {
      // this is expected
    }
  }

  @Ignore({IPHONE})
  @NeedsFreshDriver
  @Test
  public void testShouldNotBeAbleToLocateASingleElementOnABlankPage() {
    // Note we're on the default start page for the browser at this point.

    try {
      driver.findElement(By.id("nonExistantButton"));
      fail("Should not have succeeded");
    } catch (NoSuchElementException e) {
      // this is expected
    }
  }

  @Test
  public void testFindingALinkByXpathUsingContainsKeywordShouldWork() {
    driver.get(pages.nestedPage);

    try {
      driver.findElement(By.xpath("//a[contains(.,'hello world')]"));
    } catch (Exception e) {
      fail("Should not have thrown an exception");
    }
  }

  @JavascriptEnabled
  @Test
  public void testShouldBeAbleToFindAnElementByCssSelector() {
    driver.get(pages.xhtmlTestPage);
    driver.findElement(By.cssSelector("div.content"));
  }

  @JavascriptEnabled
  @Test
  public void testShouldBeAbleToFindElementsByCssSelector() {
    driver.get(pages.xhtmlTestPage);
    driver.findElements(By.cssSelector("p"));
  }

  @JavascriptEnabled
  @Test
  public void testShouldBeAbleToFindAnElementByCompoundCssSelector() {
    driver.get(pages.xhtmlTestPage);
    WebElement element = driver.findElement(By.cssSelector("div.extraDiv, div.content"));
    assertEquals("content", element.getAttribute("class"));
  }

  @JavascriptEnabled
  @Test
  public void testShouldBeAbleToFindElementsByCompoundCssSelector() {
    driver.get(pages.xhtmlTestPage);
    List<WebElement> elements = driver.findElements(By.cssSelector("div.extraDiv, div.content"));
    assertEquals("content", elements.get(0).getAttribute("class"));
    assertEquals("extraDiv", elements.get(1).getAttribute("class"));
  }

  @JavascriptEnabled
  @Test
  @Ignore(value = {IE}, reason = "IE supports only short version option[selected]")
  public void testShouldBeAbleToFindAnElementByBooleanAttributeUsingCssSelector() {
    driver.get(appServer.whereIs("locators_tests/boolean_attribute_selected.html"));
    WebElement element = driver.findElement(By.cssSelector("option[selected='selected']"));
    assertEquals("two", element.getAttribute("value"));
  }

  @JavascriptEnabled
  @Test
  public void testShouldBeAbleToFindAnElementByBooleanAttributeUsingShortCssSelector() {
    driver.get(appServer.whereIs("locators_tests/boolean_attribute_selected.html"));
    WebElement element = driver.findElement(By.cssSelector("option[selected]"));
    assertEquals("two", element.getAttribute("value"));
  }

  @JavascriptEnabled
  @Test
  public void testShouldBeAbleToFindAnElementByBooleanAttributeUsingShortCssSelectorOnHtml4Page() {
    driver.get(appServer.whereIs("locators_tests/boolean_attribute_selected_html4.html"));
    WebElement element = driver.findElement(By.cssSelector("option[selected]"));
    assertEquals("two", element.getAttribute("value"));
  }

  @Ignore(value = {ANDROID, OPERA, OPERA_MOBILE, MARIONETTE}, reason = "Just not working")
  @Test
  public void testAnElementFoundInADifferentFrameIsStale() {
    driver.get(pages.missedJsReferencePage);
    driver.switchTo().frame("inner");
    WebElement element = driver.findElement(By.id("oneline"));
    driver.switchTo().defaultContent();
    try {
      element.getText();
      fail("Expected exception");
    } catch (StaleElementReferenceException expected) {
      // Expected
    }
  }

  @JavascriptEnabled
  @Ignore({ANDROID, IPHONE, OPERA, OPERA_MOBILE, MARIONETTE})
  @Test
  public void testAnElementFoundInADifferentFrameViaJsCanBeUsed() {
    driver.get(pages.missedJsReferencePage);

    try {
      driver.switchTo().frame("inner");
      WebElement first = driver.findElement(By.id("oneline"));

      driver.switchTo().defaultContent();
      WebElement element = (WebElement) ((JavascriptExecutor) driver).executeScript(
          "return frames[0].document.getElementById('oneline');");


      driver.switchTo().frame("inner");

      WebElement second = driver.findElement(By.id("oneline"));

      assertEquals(first, element);
      assertEquals(second, element);
    } finally {
      driver.switchTo().defaultContent();
    }
  }

  @Test
  @Ignore({OPERA, MARIONETTE})
  public void findsByLinkTextOnXhtmlPage() {
    assumeFalse("Old IE doesn't render XHTML pages, don't try loading XHTML pages in it", isOldIe(driver));

    driver.get(appServer.whereIs("actualXhtmlPage.xhtml"));
    String linkText = "Foo";
    WebElement element = driver.findElement(By.linkText(linkText));
    assertEquals(linkText, element.getText());
  }

  @Ignore({REMOTE})
  @Test
  public void testLinkWithFormattingTags() {
    driver.get(pages.simpleTestPage);
    WebElement elem = driver.findElement(By.id("links"));

    WebElement res =
        elem.findElement(By.partialLinkText("link with formatting tags"));
    assertNotNull(res);
    assertEquals("link with formatting tags", res.getText());
  }

  @Ignore({REMOTE})
  @Test
  public void testLinkWithLeadingSpaces() {
    driver.get(pages.simpleTestPage);
    WebElement elem = driver.findElement(By.id("links"));

    WebElement res = elem.findElement(By.partialLinkText("link with leading space"));
    assertNotNull(res);
    assertEquals("link with leading space", res.getText());
  }

  @Ignore({REMOTE})
  @Test
  public void testLinkWithTrailingSpace() {
    driver.get(pages.simpleTestPage);
    WebElement elem = driver.findElement(By.id("links"));

    WebElement res =
        elem.findElement(By.partialLinkText("link with trailing space"));
    assertNotNull(res);
    assertEquals("link with trailing space", res.getText());
  }

  @Test
  @Ignore(MARIONETTE)
  public void testDriverCanGetLinkByLinkTestIgnoringTrailingWhitespace() {
    driver.get(pages.simpleTestPage);
    WebElement link = null;
    try {
      link = driver.findElement(By.linkText("link with trailing space"));
    } catch (NoSuchElementException e) {
      fail("Should have found link");
    }
    assertEquals("linkWithTrailingSpace", link.getAttribute("id"));
  }

  @Test
  @Ignore(MARIONETTE)
  public void testElementCanGetLinkByLinkTestIgnoringTrailingWhitespace() {
    driver.get(pages.simpleTestPage);
    WebElement elem = driver.findElement(By.id("links"));

    WebElement link = null;
    try {
      link = elem.findElement(By.linkText("link with trailing space"));
    } catch (NoSuchElementException e) {
      fail("Should have found link");
    }
    assertEquals("linkWithTrailingSpace", link.getAttribute("id"));
  }

  @Test
  public void testShouldThrowAnExceptionWhenThereIsNoLinkToClick() {
    driver.get(pages.xhtmlTestPage);

    try {
      driver.findElement(By.xpath("//a[@id='Not here']"));
      fail("Should not have succeeded");
    } catch (NoSuchElementException e) {
      // this is expected
    }
  }

  @Test
  public void testShouldFindSingleElementByXPath() {
    driver.get(pages.xhtmlTestPage);
    WebElement element = driver.findElement(By.xpath("//h1"));
    assertThat(element.getText(), equalTo("XHTML Might Be The Future"));
  }

  @Test
  public void testShouldFindElementsByXPath() {
    driver.get(pages.xhtmlTestPage);
    List<WebElement> divs = driver.findElements(By.xpath("//div"));
    assertThat(divs.size(), equalTo(13));
  }

  @Test
  public void testShouldBeAbleToFindManyElementsRepeatedlyByXPath() {
    driver.get(pages.xhtmlTestPage);
    String xpathString = "//node()[contains(@id,'id')]";
    assertThat(driver.findElements(By.xpath(xpathString)).size(), equalTo(3));

    xpathString = "//node()[contains(@id,'nope')]";
    assertThat(driver.findElements(By.xpath(xpathString)).size(), equalTo(0));
  }

  @Test
  public void testShouldBeAbleToIdentifyElementsByClass() {
    driver.get(pages.xhtmlTestPage);

    String header = driver.findElement(By.xpath("//h1[@class='header']")).getText();
    assertThat(header, equalTo("XHTML Might Be The Future"));
  }

  @Test
  public void testShouldBeAbleToSearchForMultipleAttributes() {
    driver.get(pages.formPage);

    try {
      driver.findElement(
          By.xpath("//form[@name='optional']/input[@type='submit' and @value='Click!']")).click();
    } catch (NoSuchElementException e) {
      fail("Should be able to find the submit button");
    }
  }

  @Test
  public void testShouldLocateElementsWithGivenText() {
    driver.get(pages.xhtmlTestPage);

    try {
      driver.findElement(By.xpath("//a[text()='click me']"));
    } catch (NoSuchElementException e) {
      e.printStackTrace();
      fail("Cannot find the element");
    }
  }

  @Ignore({ANDROID, IPHONE, OPERA, OPERA_MOBILE, MARIONETTE})
  @Test
  public void testShouldThrowInvalidSelectorExceptionWhenXPathIsSyntacticallyInvalidInDriverFindElement() {
    driver.get(pages.formPage);

    try {
      driver.findElement(By.xpath("this][isnot][valid"));
      fail("Should not have succeeded because the xpath expression is syntactically not correct");
    } catch (RuntimeException e) {
      // We expect an InvalidSelectorException because the xpath expression is syntactically invalid
      assertThat(e, is(instanceOf(InvalidSelectorException.class)));
    }
  }

  @Ignore({ANDROID, IPHONE, OPERA, OPERA_MOBILE, MARIONETTE})
  @Test
  public void testShouldThrowInvalidSelectorExceptionWhenXPathIsSyntacticallyInvalidInDriverFindElements() {
    assumeFalse("Ignoring xpath error test in IE6", TestUtilities.isIe6(driver));

    driver.get(pages.formPage);

    try {
      driver.findElements(By.xpath("this][isnot][valid"));
      fail("Should not have succeeded because the xpath expression is syntactically not correct");
    } catch (RuntimeException e) {
      // We expect an InvalidSelectorException because the xpath expression is syntactically
      // invalid
      assertThat(e, is(instanceOf(InvalidSelectorException.class)));
    }
  }

  @Ignore({ANDROID, IPHONE, OPERA, OPERA_MOBILE, MARIONETTE})
  @Test
  public void testShouldThrowInvalidSelectorExceptionWhenXPathIsSyntacticallyInvalidInElementFindElement() {
    driver.get(pages.formPage);
    WebElement body = driver.findElement(By.tagName("body"));
    try {
      body.findElement(By.xpath("this][isnot][valid"));
      fail("Should not have succeeded because the xpath expression is syntactically not correct");
    } catch (RuntimeException e) {
      // We expect an InvalidSelectorException because the xpath expression is syntactically invalid
      assertThat(e, is(instanceOf(InvalidSelectorException.class)));
    }
  }

  @Ignore({ANDROID, IPHONE, OPERA, OPERA_MOBILE, MARIONETTE})
  @Test
  public void testShouldThrowInvalidSelectorExceptionWhenXPathIsSyntacticallyInvalidInElementFindElements() {
    assumeFalse("Ignoring xpath error test in IE6", TestUtilities.isIe6(driver));

    driver.get(pages.formPage);
    WebElement body = driver.findElement(By.tagName("body"));
    try {
      body.findElements(By.xpath("this][isnot][valid"));
      fail("Should not have succeeded because the xpath expression is syntactically not correct");
    } catch (RuntimeException e) {
      // We expect an InvalidSelectorException because the xpath expression is syntactically invalid
      assertThat(e, is(instanceOf(InvalidSelectorException.class)));
    }
  }

  @Ignore({ANDROID, IPHONE, OPERA, OPERA_MOBILE, MARIONETTE})
  @Test
  public void testShouldThrowInvalidSelectorExceptionWhenXPathReturnsWrongTypeInDriverFindElement() {
    driver.get(pages.formPage);

    try {
      driver.findElement(By.xpath("count(//input)"));
      fail("Should not have succeeded because the xpath expression does not select an element");
    } catch (RuntimeException e) {
      // We expect an exception because the XPath expression results in a number, not in an element
      assertThat(e, is(instanceOf(InvalidSelectorException.class)));
    }
  }

  @Ignore({ANDROID, IPHONE, OPERA, OPERA_MOBILE, MARIONETTE})
  @Test
  public void testShouldThrowInvalidSelectorExceptionWhenXPathReturnsWrongTypeInDriverFindElements() {
    assumeFalse("Ignoring xpath error test in IE6", TestUtilities.isIe6(driver));

    driver.get(pages.formPage);

    try {
      driver.findElements(By.xpath("count(//input)"));
      fail("Should not have succeeded because the xpath expression does not select an element");
    } catch (RuntimeException e) {
      // We expect an exception because the XPath expression results in a number, not in an element
      assertThat(e, is(instanceOf(InvalidSelectorException.class)));
    }
  }

  @Ignore({ANDROID, IPHONE, OPERA, OPERA_MOBILE, MARIONETTE})
  @Test
  public void testShouldThrowInvalidSelectorExceptionWhenXPathReturnsWrongTypeInElementFindElement() {
    driver.get(pages.formPage);

    WebElement body = driver.findElement(By.tagName("body"));

    try {
      body.findElement(By.xpath("count(//input)"));
      fail("Should not have succeeded because the xpath expression does not select an element");
    } catch (RuntimeException e) {
      // We expect an exception because the XPath expression results in a number, not in an element
      assertThat(e, is(instanceOf(InvalidSelectorException.class)));
    }
  }

  @Ignore({ANDROID, IPHONE, OPERA, OPERA_MOBILE, MARIONETTE})
  @Test
  public void testShouldThrowInvalidSelectorExceptionWhenXPathReturnsWrongTypeInElementFindElements() {
    assumeFalse("Ignoring xpath error test in IE6", TestUtilities.isIe6(driver));

    driver.get(pages.formPage);

    WebElement body = driver.findElement(By.tagName("body"));

    try {
      body.findElements(By.xpath("count(//input)"));
      fail("Should not have succeeded because the xpath expression does not select an element");
    } catch (RuntimeException e) {
      // We expect an exception because the XPath expression results in a number, not in an element
      assertThat(e, is(instanceOf(InvalidSelectorException.class)));
    }
  }

}
