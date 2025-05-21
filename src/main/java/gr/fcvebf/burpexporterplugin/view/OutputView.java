package gr.fcvebf.burpexporterplugin.view;

import gr.fcvebf.burpexporterplugin.models.IssuesModel;

public interface OutputView
{
    void exportIssues(IssuesModel issues);
}