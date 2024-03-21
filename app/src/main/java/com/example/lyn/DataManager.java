package com.example.lyn;

public class DataManager {
    private static DataManager instance;
    private String retrievedSNo;
    private String retrievedFirstName;
    private String retrievedLastName;
    private String retrievedEmail;
    private String retrievedClass;
    private String retrievedGroupID;
    private String retrievedMentorID;
    private String retrievedRegID;
    private String eventType;
    private String eventName;
    private String eventTime;
    private String meetingUrl;
    private String id;
    private String examID;
    private String subjectName;
    private String examName;
    private String className;
    private String examType;
    private String qpLink;
    private String examSubjectid;
    private String retrievedMedium;

    private DataManager() {
        // Private constructor to prevent instantiation from outside
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public String getRetrievedSNo() {
        return retrievedSNo;
    }

    public void setRetrievedSNo(String retrievedSNo) {
        this.retrievedSNo = retrievedSNo;
    }

    public String getRetrievedFirstName() {
        return retrievedFirstName;
    }

    public void setRetrievedFirstName(String retrievedFirstName) {
        this.retrievedFirstName = retrievedFirstName;
    }

    public String getRetrievedLastName() {
        return retrievedLastName;
    }

    public void setRetrievedLastName(String retrievedLastName) {
        this.retrievedLastName = retrievedLastName;
    }

    public String getRetrievedEmail() {
        return retrievedEmail;
    }

    public void setRetrievedEmail(String retrievedEmail) {
        this.retrievedEmail = retrievedEmail;
    }

    public String getRetrievedClass() {
        return retrievedClass;
    }

    public void setRetrievedClass(String retrievedClass) {
        this.retrievedClass = retrievedClass;
    }

    public String getRetrievedGroupID() {
        return retrievedGroupID;
    }

    public void setRetrievedGroupID(String retrievedGroupID) {
        this.retrievedGroupID = retrievedGroupID;
    }

    public String getRetrievedMentorID() {
        return retrievedMentorID;
    }

    public void setRetrievedMentorID(String retrievedMentorID) {
        this.retrievedMentorID = retrievedMentorID;
    }

    public String getRetrievedRegID() {
        return retrievedRegID;
    }

    public void setRetrievedRegID(String retrievedRegID) {
        this.retrievedRegID = retrievedRegID;
    }
    public String getRetrievedMedium() {
        return retrievedMedium;
    }
    public void setRetrievedMedium(String retrievedMedium) {
        this.retrievedMedium = retrievedMedium;
    }
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getMeetingUrl() {
        return meetingUrl;
    }

    public void setMeetingUrl(String meetingUrl) {
        this.meetingUrl = meetingUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExamID() {
        return examID;
    }

    public void setExamID(String examID) {
        this.examID = examID;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public String getQPLink() {
        return qpLink;
    }

    public void setQPLink(String qpLink) {
        this.qpLink = qpLink;
    }

    public String getExamSubjectid() {
        return examSubjectid;
    }

    public void setExamSubjectid(String examSubjectid) {
        this.examSubjectid = examSubjectid;
    }
}
