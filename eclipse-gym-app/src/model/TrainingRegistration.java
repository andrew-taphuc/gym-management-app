package model;

import java.time.LocalDate;

public class TrainingRegistration {
    private int registrationId;
    private int memberId;
    private int planId;
    private int trainerId;
    private LocalDate startDate;
    private int sessionsLeft;
    private int paymentId;
    private Member member; // Reference to Member
    private TrainingPlan plan; // Reference to TrainingPlan
    private Trainer trainer; // Reference to Trainer
    private Payment payment; // Reference to Payment

    public TrainingRegistration() {
    }

    public TrainingRegistration(int registrationId, int memberId, int planId, int trainerId,
            LocalDate startDate, int sessionsLeft, int paymentId) {
        this.registrationId = registrationId;
        this.memberId = memberId;
        this.planId = planId;
        this.trainerId = trainerId;
        this.startDate = startDate;
        this.sessionsLeft = sessionsLeft;
        this.paymentId = paymentId;
    }

    // Getters and Setters
    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public int getSessionsLeft() {
        return sessionsLeft;
    }

    public void setSessionsLeft(int sessionsLeft) {
        this.sessionsLeft = sessionsLeft;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public TrainingPlan getPlan() {
        return plan;
    }

    public void setPlan(TrainingPlan plan) {
        this.plan = plan;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}