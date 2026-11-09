package uk.ac.york.eng1.goosepatrol.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MessagesTest {

    @Test
    void anOrdinaryMessageReplacesTheLastOne() {
        Messages messages = new Messages();
        messages.show("first");
        messages.show("second");
        assertEquals("second", messages.getText());
    }

    @Test
    void aMessageDisappearsAfterItsTime() {
        Messages messages = new Messages();
        messages.show("hello");
        messages.update(Messages.MESSAGE_SECONDS - 0.1f);
        assertTrue(messages.isShowing());
        messages.update(0.2f);
        assertFalse(messages.isShowing());
        assertEquals("", messages.getText());
    }

    @Test
    void anEventMessageIsNotReplacedWhileItIsBeingRead() {
        Messages messages = new Messages();
        messages.showImportant("Hidden event: a tall duck");
        messages.update(1f);
        messages.show("A goose snatched a snack!");
        assertEquals("Hidden event: a tall duck", messages.getText());
    }

    @Test
    void theNewestWaitingMessageIsShownAfterTheEventMessage() {
        Messages messages = new Messages();
        messages.showImportant("Hidden event");
        messages.show("Wave 2 of 3");
        messages.show("A goose snatched a snack!");
        messages.update(Messages.HOLD_SECONDS);
        assertEquals("A goose snatched a snack!", messages.getText());
        messages.update(Messages.MESSAGE_SECONDS - 0.1f);
        assertTrue(messages.isShowing());
    }

    @Test
    void anEventMessageStaysLongerWhenNothingElseComes() {
        Messages messages = new Messages();
        messages.showImportant("Hidden event");
        messages.update(Messages.IMPORTANT_SECONDS - 0.1f);
        assertEquals("Hidden event", messages.getText());
    }

    @Test
    void aNewEventMessageReplacesAnOlderOne() {
        Messages messages = new Messages();
        messages.showImportant("backpack");
        messages.update(1f);
        messages.showImportant("duck");
        assertEquals("duck", messages.getText());
    }
}
