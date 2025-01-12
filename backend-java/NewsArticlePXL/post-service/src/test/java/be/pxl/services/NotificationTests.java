package be.pxl.services;
import be.pxl.services.domain.Notification;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class NotificationTests {
    @Test
    void shouldCreateNotificationUsingBuilder() {
        Notification notification = Notification.builder()
                .id(1L)
                .message("Test Message")
                .author("Test Author")
                .postAuthor("Test Post Author")
                .build();

        assertThat(notification.getId()).isEqualTo(1L);
        assertThat(notification.getMessage()).isEqualTo("Test Message");
        assertThat(notification.getAuthor()).isEqualTo("Test Author");
        assertThat(notification.getPostAuthor()).isEqualTo("Test Post Author");
    }

    @Test
    void shouldSetAndGetValues() {
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setMessage("Test Message");
        notification.setAuthor("Test Author");
        notification.setPostAuthor("Test Post Author");

        assertThat(notification.getId()).isEqualTo(1L);
        assertThat(notification.getMessage()).isEqualTo("Test Message");
        assertThat(notification.getAuthor()).isEqualTo("Test Author");
        assertThat(notification.getPostAuthor()).isEqualTo("Test Post Author");
    }

    @Test
    void shouldCreateNotificationWithEmptyBuilder() {
        Notification notification = Notification.builder().build();

        assertThat(notification).isNotNull();
        assertThat(notification.getId()).isNull();
        assertThat(notification.getMessage()).isNull();
        assertThat(notification.getAuthor()).isNull();
        assertThat(notification.getPostAuthor()).isNull();
    }

    @Test
    void shouldCreateNotificationUsingDefaultConstructorOnly() {
        Notification notification = new Notification(); // Alleen de default constructor aanroepen

        assertThat(notification).isNotNull();
        assertThat(notification.getId()).isNull();
        assertThat(notification.getMessage()).isNull();
        assertThat(notification.getAuthor()).isNull();
        assertThat(notification.getPostAuthor()).isNull();
    }
}