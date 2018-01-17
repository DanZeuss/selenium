package org.openqa.selenium.remote.server.scheduler;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openqa.selenium.testing.Assertions.assertException;

import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.NewSessionPayload;
import org.openqa.selenium.remote.server.ActiveSession;
import org.openqa.selenium.remote.server.ServicedSession;
import org.openqa.selenium.remote.server.SessionFactory;

import java.io.IOException;
import java.util.Optional;

public class SchedulerTest {

  @Test
  public void bootstrap() throws IOException {
    Scheduler.Builder builder = Scheduler.builder();

    assertException(
        builder::create,
        e -> assertTrue(e.getMessage(), e.getMessage().toLowerCase().contains("distributor")));

    SessionFactory factory = new ServicedSession.Factory(
        caps -> "firefox".equals(caps.getBrowserName()),
        GeckoDriverService.class.getName());
    Host host = Host.builder().name("localhost").add(factory).create();

    Distributor distributor = new Distributor().add(host);

    builder.distributeUsing(distributor);
    Scheduler scheduler = builder.create();

    try (NewSessionPayload payload = NewSessionPayload.create(new FirefoxOptions())) {
      ActiveSession session = scheduler.createSession(payload);
      session.stop();
    }
  }

  // This is the original behaviour from Grid. Try, and then fail.
  @Test
  public void byDefaultASchedulerDoesNotAttemptToRescheduleSessions() throws IOException {
    SessionFactory factory = mock(SessionFactory.class);
    when(factory.isSupporting(any())).thenReturn(true);
    Mockito.when(factory.apply(any(), any())).thenReturn(Optional.empty());

    Host host = Host.builder()
        .name("localhost")
        .add(factory)
        .create();

    Distributor distributor = new Distributor().add(host);

    Scheduler scheduler = Scheduler.builder()
        .distributeUsing(distributor)
        .create();

    try (NewSessionPayload payload = NewSessionPayload.create(new FirefoxOptions())) {
      assertException(
          () -> scheduler.createSession(payload),
          e -> assertTrue(e instanceof SessionNotCreatedException));
    }

    verify(factory, times(1)).apply(any(), any());
  }

  @Test
  public void attemptingToStartASessionWhichFailsMarksTheSessionFactoryAsAvailable() {
    fail("Write me");
  }

  @Test
  public void onceASessionStartsTheAssociatedSessionFactoryBecomesUnavailable() {
    fail("Write me");
  }

  @Test
  public void onceASessionStopsTheAssociatedSessionFactoryBecomesAvailable() {
    fail("Write me");
  }

  @Test
  public void callingQuitOnTheWrappedDriverShouldQuitTheSessionAndMakeTheFactoryAvailable() {
    fail("Write me");
  }

  @Test
  public void shouldAllowATimedFallback() {
    fail("Write me");
  }

}
