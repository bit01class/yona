package models;

import static org.fest.assertions.Assertions.assertThat;
import models.enumeration.IssueState;
import models.enumeration.IssueStateType;

import org.junit.Test;

import utils.JodaDateUtil;

import com.avaje.ebean.Page;

public class IssueTest extends ModelTest {

    @Test
    public void create() throws Exception {
        // Given
        Issue issue = new Issue();
        issue.title = "불필요한 로그 출력 코드 제거test";
        issue.date = JodaDateUtil.today();
        issue.state = IssueState.ASSIGNED;
        issue.reporterId = User.findById(1l).id;
        // When
        // Then
        assertThat(Issue.create(issue)).isEqualTo(5l);
    }

    @Test
    public void findById() throws Exception {
        // Given
        Issue issue = new Issue();
        issue.title = "불필요한 로그 출력 코드 제거";
        issue.date = JodaDateUtil.today();
        issue.state = IssueState.FINISHED;
        issue.reporterId = User.findById(1l).id;
        Long id = Issue.create(issue);
        // When
        Issue issueTest = Issue.findById(id);
        // Then
        assertThat(issueTest).isNotNull();
    }

    @Test
    public void delete() {
        // Given
        // When
        Issue.delete(4l);
        // Then
        assertThat(Issue.findById(4l)).isNull();
    }

    @Test
    public void findOpenIssues() throws Exception {
        // Given
        // When
        Page<Issue> issues = Issue.findOpenIssues("nForge4java");
        // Then
        assertThat(issues.getTotalRowCount()).isEqualTo(1);
    }

    @Test
    public void findClosedIssues() throws Exception {
        // Given
        // When
        Page<Issue> issues = Issue.findClosedIssues("nForge4java");
        // Then
        assertThat(issues.getTotalRowCount()).isEqualTo(1);
    }

    @Test
    public void findFilterIssues() throws Exception {

        // Given
        // When
        Page<Issue> issues = Issue.findFilteredIssues("nForge4java", "로그",
                IssueStateType.OPEN, true, true);
        // Then
        assertThat(issues.getTotalRowCount()).isEqualTo(1);

    }

    @Test
    public void findCommentedIssue() throws Exception {
        // Given
        // When
        Page<Issue> issues = Issue.findCommentedIssues("nForge4java", "");
        // Then
        assertThat(issues.getTotalRowCount()).isEqualTo(1);
    }

    @Test
    public void findFileAttachedIssue() throws Exception {
        // Given
        // When
        Page<Issue> issues = Issue.findFileAttachedIssues("nForge4java", "");
        // Then
        assertThat(issues.getTotalRowCount()).isEqualTo(1);
    }

    @Test
    public void findAssigneeByIssueId() {
        // Given
        Issue issueT = new Issue();
        issueT.id = 10l;
        issueT.project = Project.findById(1l);
        issueT.body = "test";
        issueT.title = "test";
        issueT.assigneeId = 1l;

        // When
        Long assignee = Issue.findAssigneeIdByIssueId(1l, 1l);
        // Then

        assertThat(assignee).isEqualTo(1l);
        // assertThat(assignee.loginId).isEqualTo("hobi");
        // assertThat(assignee.name).isEqualTo("hobi");

    }

}
