package com.example.jobrunr.config;

import com.example.jobrunr.utils.AppConstants;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.scheduling.cron.Cron;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile({AppConstants.PROFILE_NOT_TEST, AppConstants.PROFILE_NOT_PROD})
public class Initializer implements CommandLineRunner {

    private final JobScheduler jobScheduler;

    @Override
    public void run(String... args) {
        log.info("Running Initializer.....");
        jobScheduler.enqueue(this::doFireAndForgetWork);
        log.info("Completed setting Fire and Forget using JobScheduler");
        BackgroundJob.enqueue(UUID.randomUUID(), this::doFireAndForgetWork);
        log.info("Completed setting Fire and Forget BackgroundJob");
        BackgroundJob.schedule(Instant.now().plusMillis(1), this::doSomeWork);
        log.info("Completed Scheduling Background Delayed Job");
        BackgroundJob.scheduleRecurrently(Cron.every15seconds(), this::doSomeWork);
        log.info("Completed Scheduling Recurrently BackgroundJob");
        BackgroundJob.scheduleRecurrently(Cron.every30seconds(), this::doWorkWithCustomJobFilters);
        log.info("Completed Scheduling Recurrently BackgroundJob with 2 retries");
    }

    @Job(name = "doSomeWork")
    public void doSomeWork() {
        log.info("Hi, I am from BackgroundJob scheduling at {}", LocalDateTime.now());
    }

    @Job(name = "doFireAndForgetWork")
    public void doFireAndForgetWork() {
        log.info("Hi, I am from FireAndForget BackgroundJob scheduling at {}", LocalDateTime.now());
    }

    @Job(name = "someJobName", retries = 2)
    public void doWorkWithCustomJobFilters() {
        log.info("Hi, I will be retried only twice ");
    }
}
