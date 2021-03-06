/**
 * Yobi, Project Hosting SW
 *
 * Copyright 2012 NAVER Corp.
 * http://yobi.io
 *
 * @author Tae
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package models;

import models.enumeration.ResourceType;
import models.resource.Resource;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class IssueComment extends Comment {
    private static final long serialVersionUID = 1L;
    public static final Finder<Long, IssueComment> find = new Finder<>(Long.class, IssueComment.class);

    @ManyToOne
    public Issue issue;

    @OneToOne
    private IssueComment parentComment;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "issue_comment_voter",
            joinColumns = @JoinColumn(name = "issue_comment_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    public Set<User> voters = new HashSet<>();

    public IssueComment(Issue issue, User author, String contents) {
        super(author, contents);
        this.issue = issue;
        this.projectId = issue.project.id;
    }

    /**
     * @see Comment#getParent()
     */
    public AbstractPosting getParent() {
        return issue;
    }

    @Override
    public IssueComment getParentComment() {
        return this.parentComment;
    }

    @Override
    public void setParentComment(Comment comment) {
        this.parentComment = (IssueComment)comment;
    }

    @Override
    public List<IssueComment> getSiblingComments() {
        if (parentComment == null) {
            return null;
        }

        List<IssueComment> comments = find.where()
                .eq("parentComment.id", parentComment.id)
                .findList();
        return comments;
    }

    @Override
    public List<IssueComment> getChildComments() {
        List<IssueComment> comments = find.where()
                .eq("parentComment.id", id)
                .findList();
        return comments;
    }

    /**
     * @see Comment#asResource()
     */
    @Override
    public Resource asResource() {
        return new Resource() {
            @Override
            public String getId() {
                return id.toString();
            }

            @Override
            public Project getProject() {
                return issue.project;
            }

            @Override
            public ResourceType getType() {
                return ResourceType.ISSUE_COMMENT;
            }

            @Override
            public Long getAuthorId() {
                return authorId;
            }

            @Override
            public Resource getContainer() {
                return issue.asResource();
            }
        };
    }

    public void addVoter(User user) {
        if (voters.add(user)) {
            update();
        }
    }

    public void removeVoter(User user) {
        if (voters.remove(user)) {
            update();
        }
    }
}
