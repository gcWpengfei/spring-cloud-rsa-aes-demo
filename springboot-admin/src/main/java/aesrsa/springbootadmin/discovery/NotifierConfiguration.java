package aesrsa.springbootadmin.discovery;

import de.codecentric.boot.admin.notify.Notifier;
import de.codecentric.boot.admin.notify.RemindingNotifier;
import de.codecentric.boot.admin.notify.filter.FilteringNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
public class NotifierConfiguration {
  @Autowired
  private Notifier delegate;

  @Bean
  public FilteringNotifier filteringNotifier() {
    return new FilteringNotifier(delegate);
  }

  @Bean
  @Primary
  public RemindingNotifier remindingNotifier() {
    RemindingNotifier notifier = new RemindingNotifier(filteringNotifier());
    notifier.setReminderPeriod(TimeUnit.SECONDS.toMillis(10));
    return notifier;
  }

  @Scheduled(fixedRate = 1_000L)
  public void remind() {
    remindingNotifier().sendReminders();
  }
}