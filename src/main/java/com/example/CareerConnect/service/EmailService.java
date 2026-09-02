package com.example.CareerConnect.service;

import com.example.CareerConnect.entity.Job;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final Resend resend;
    private final TemplateEngine templateEngine;

    @Value("${resend.from-email}")
    private String fromEmail;

    public EmailService(
            @Value("${RESEND_API_KEY}") String resendApiKey,
            TemplateEngine templateEngine) {

        this.resend = new Resend(resendApiKey);
        this.templateEngine = templateEngine;
    }

    // ==========================================================
    // PLAIN TEXT EMAIL
    // ==========================================================

    public void sendEmail(String to, String subject, String body) {

        try {

            CreateEmailOptions request = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(to)
                    .subject(subject)
                    .text(body)
                    .build();

            resend.emails().send(request);

            System.out.println(
                    "Plain text email sent successfully to: " + to
            );

        } catch (ResendException e) {

            System.err.println(
                    "Failed to send plain text email to "
                            + to
                            + ": "
                            + e.getMessage()
            );

            throw new RuntimeException(
                    "Email sending failed",
                    e
            );
        }
    }

    // ==========================================================
    // HTML EMAIL
    // ==========================================================

    public void sendHtmlEmail(
            String to,
            String subject,
            String templateName,
            Context context) {

        try {

            String htmlContent =
                    templateEngine.process(
                            templateName,
                            context
                    );

            CreateEmailOptions request =
                    CreateEmailOptions.builder()
                            .from(fromEmail)
                            .to(to)
                            .subject(subject)
                            .html(htmlContent)
                            .build();

            resend.emails().send(request);

            System.out.println(
                    "HTML email sent successfully to: " + to
            );

        } catch (ResendException e) {

            System.err.println(
                    "Failed to send HTML email to "
                            + to
                            + ": "
                            + e.getMessage()
            );

            throw new RuntimeException(
                    "Email sending failed",
                    e
            );

        } catch (Exception e) {

            System.err.println(
                    "Failed to process email template: "
                            + e.getMessage()
            );

            throw new RuntimeException(
                    "Email template processing failed",
                    e
            );
        }
    }

    // ==========================================================
    // JOB STATUS EMAIL
    // ==========================================================

    public void sendJobStatusEmail(
            String to,
            String candidateName,
            Job job,
            String status) {

        Context context = new Context();

        context.setVariable(
                "candidateName",
                candidateName
        );

        context.setVariable(
                "jobTitle",
                job.getTitle()
        );

        context.setVariable(
                "category",
                job.getCategory()
        );

        context.setVariable(
                "location",
                job.getLocation()
        );

        context.setVariable(
                "experience",
                job.getExperience()
        );

        context.setVariable(
                "salary",
                job.getSalary()
        );

        context.setVariable(
                "skills",
                job.getSkills()
        );

        context.setVariable(
                "description",
                job.getDescription()
        );

        context.setVariable(
                "companyName",
                "CareerConnect"
        );

        String subject =
                "Application Update - CareerConnect";

        String templateName;

        switch (status.toUpperCase()) {

            case "SHORTLISTED":
                templateName = "emails/shortlisted";
                break;

            case "REJECTED":
                templateName = "emails/rejected";
                break;

            case "PENDING":
                templateName = "emails/pending";
                break;

            default:
                throw new IllegalArgumentException(
                        "Invalid status: " + status
                );
        }

        sendHtmlEmail(
                to,
                subject,
                templateName,
                context
        );
    }

    // ==========================================================
    // SHORTLIST NOTIFICATION
    // ==========================================================

    public void sendShortlistNotification(
            String to,
            String studentName,
            String jobTitle,
            String employerName) {

        Job dummyJob = new Job();

        dummyJob.setTitle(jobTitle);
        dummyJob.setCategory("N/A");
        dummyJob.setLocation("N/A");
        dummyJob.setExperience("N/A");
        dummyJob.setSalary("N/A");
        dummyJob.setSkills("N/A");
        dummyJob.setDescription("N/A");

        sendJobStatusEmail(
                to,
                studentName,
                dummyJob,
                "SHORTLISTED"
        );
    }

    // ==========================================================
    // REJECTION NOTIFICATION
    // ==========================================================

    public void sendRejectionNotification(
            String to,
            String studentName,
            String jobTitle,
            String employerName) {

        Job dummyJob = new Job();

        dummyJob.setTitle(jobTitle);
        dummyJob.setCategory("N/A");
        dummyJob.setLocation("N/A");
        dummyJob.setExperience("N/A");
        dummyJob.setSalary("N/A");
        dummyJob.setSkills("N/A");
        dummyJob.setDescription("N/A");

        sendJobStatusEmail(
                to,
                studentName,
                dummyJob,
                "REJECTED"
        );
    }

    // ==========================================================
    // OTP EMAIL
    // ==========================================================

    public void sendOtpEmail(
            String to,
            String otp) {

        Context context = new Context();

        context.setVariable(
                "otp",
                otp
        );

        context.setVariable(
                "companyName",
                "CareerConnect"
        );

        String subject =
                "Password Reset OTP - CareerConnect";

        sendHtmlEmail(
                to,
                subject,
                "emails/otp",
                context
        );
    }

    // ==========================================================
    // PASSWORD RESET CONFIRMATION
    // ==========================================================

    public void sendPasswordResetConfirmationEmail(
            String to) {

        Context context = new Context();

        context.setVariable(
                "companyName",
                "CareerConnect"
        );

        String subject =
                "Password Reset Successful - CareerConnect";

        sendHtmlEmail(
                to,
                subject,
                "emails/password_reset_success",
                context
        );
    }
}