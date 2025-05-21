package gr.fcvebf.burpexporterplugin.models;

import burp.api.montoya.scanner.audit.issues.AuditIssue;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableList;

import static gr.fcvebf.burpexporterplugin.models.Finding.CONFIDENCE_ORDER;
import static gr.fcvebf.burpexporterplugin.models.Finding.SEVERITY_ORDER;
import gr.fcvebf.burpexporterplugin.view.burp.BurpIcon;
import gr.fcvebf.burpexporterplugin.view.burp.BurpIconFile;
import static gr.fcvebf.burpexporterplugin.view.burp.BurpIcon.Builder.icon;
import static gr.fcvebf.burpexporterplugin.view.burp.BurpIconFile.SCAN_ISSUE_FALSE_POSITIVE;
import static gr.fcvebf.burpexporterplugin.view.burp.BurpIconFile.SCAN_ISSUE_HIGH_CERTAIN;
import static gr.fcvebf.burpexporterplugin.view.burp.BurpIconFile.SCAN_ISSUE_HIGH_FIRM;
import static gr.fcvebf.burpexporterplugin.view.burp.BurpIconFile.SCAN_ISSUE_HIGH_TENTATIVE;
import static gr.fcvebf.burpexporterplugin.view.burp.BurpIconFile.SCAN_ISSUE_INFO_CERTAIN;
import static gr.fcvebf.burpexporterplugin.view.burp.BurpIconFile.SCAN_ISSUE_INFO_FIRM;
import static gr.fcvebf.burpexporterplugin.view.burp.BurpIconFile.SCAN_ISSUE_INFO_TENTATIVE;
import static gr.fcvebf.burpexporterplugin.view.burp.BurpIconFile.SCAN_ISSUE_LOW_CERTAIN;
import static gr.fcvebf.burpexporterplugin.view.burp.BurpIconFile.SCAN_ISSUE_LOW_FIRM;
import static gr.fcvebf.burpexporterplugin.view.burp.BurpIconFile.SCAN_ISSUE_LOW_TENTATIVE;
import static gr.fcvebf.burpexporterplugin.view.burp.BurpIconFile.SCAN_ISSUE_MEDIUM_CERTAIN;
import static gr.fcvebf.burpexporterplugin.view.burp.BurpIconFile.SCAN_ISSUE_MEDIUM_FIRM;
import static gr.fcvebf.burpexporterplugin.view.burp.BurpIconFile.SCAN_ISSUE_MEDIUM_TENTATIVE;
import static java.util.Arrays.stream;


public class IssuesModel extends AbstractTableModel
{
    private enum Column
    {

        BASE_URL(0,"Base URL",JLabel.class,IssuesModel::topURL),
        ISSUE_TYPE(1, "Issue type", JLabel.class, IssuesModel::issueType),
        SEVERITY(2,"Severity", JLabel.class,IssuesModel::severity),
        CONFIDENCE(3,"Confidence", JLabel.class,IssuesModel::confidence),
        PATH(4, "URL", JLabel.class, IssuesModel::getURL),
        ;

        private final int index;
        private final String header;
        private final Class<?> columnClass;
        private final Function<Finding, ?> valueExtractor;

        <T> Column(int index, String header, Class<T> columnClass, Function<Finding, T> valueExtractor)
        {
            this.index = index;
            this.header = header;
            this.columnClass = columnClass;
            this.valueExtractor = valueExtractor;
        }

        static Column getColumnAtIndex(int index)
        {
            for (Column column : values())
            {
                if (column.index == index)
                {
                    return column;
                }
            }

            throw new IllegalArgumentException("No column found for index: " + index);
        }

    }

    private final List<AuditIssue> issues;
    private List<Finding> findings;
    private final SortedSet<Integer> selectedRows;

    public IssuesModel()
    {
        this.issues = new ArrayList<>();
        this.findings= new ArrayList<>();
        this.selectedRows = new TreeSet<>(Comparator.reverseOrder());
    }


    public List<AuditIssue> getIssues()
    {
        return unmodifiableList(issues);
    }



    public List<Finding> getFindings()
    {
        return unmodifiableList(findings);
    }

    public void addIssues(List<AuditIssue> issuesToAdd)
    {
        try {
            Set<AuditIssue> existingIssues = new HashSet<>(issues);
            Set<Finding> existingFindings = new HashSet<>(findings);

            int added = 0;
            for (AuditIssue auditIssue : issuesToAdd) {
                if (!existingIssues.contains(auditIssue)) {
                    issues.add(auditIssue);
                    added++;
                }
            }

            List<Finding> final_list = Finding.getUniqueFindingsList(issues);

            //Sort by Severity,Confidence, Name
            // Create the primary comparator (severity)
            Comparator<Finding> severityComparator = Comparator.comparingInt(f ->
                    SEVERITY_ORDER.getOrDefault(f.getSeverity(), Integer.MAX_VALUE)
            );

            // Create the secondary comparator (confidence)
            Comparator<Finding> confidenceComparator = Comparator.comparingInt(f ->
                    CONFIDENCE_ORDER.getOrDefault(f.getConfidence(), Integer.MAX_VALUE)
            );
            final_list.sort(severityComparator
                    .thenComparing(confidenceComparator)
                    .thenComparing(Finding::getIssueName) // Add a tie-breaker, e.g., by issue name alphabetically
            );


            findings = final_list;

            if (added > 0)
            {
                fireTableRowsInserted(0, final_list.size() - 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }







    public AuditIssue getAuditIssue(List<AuditIssue> issuesList,AuditIssue cur_issue)
    {
        for (AuditIssue issue : issuesList) {
            if (issue.httpService().toString() == cur_issue.httpService().toString() && issue.name() == cur_issue.name())
            {
                return issue;
            }
        }
        return null;
    }



    public void setSelectedRows(int[] selectedRows)
    {
        this.selectedRows.clear();
        stream(selectedRows).forEach(this.selectedRows::add);
    }

    public void removeSelectedIssues()
    {
        for (Integer selectedRow : selectedRows)
        {
            issues.remove((int) selectedRow);
        }

        fireTableDataChanged();
    }


    public void removeAllIssues()
    {
        issues.removeAll(issues);
        findings.removeAll(findings);
        fireTableDataChanged();
    }


    @Override
    public int getRowCount()
    {
        //return issues.size();
        return findings.size();
    }

    @Override
    public int getColumnCount()
    {
        return Column.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        //return Column.getColumnAtIndex(columnIndex).valueExtractor.apply(issues.get(rowIndex));
        return Column.getColumnAtIndex(columnIndex).valueExtractor.apply(findings.get(rowIndex));
    }

    @Override
    public String getColumnName(int column)
    {
        return Column.getColumnAtIndex(column).header;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {

        return JLabel.class;
    }



    private static JLabel issueType(Finding finding)
    {
        JLabel label = new JLabel(finding.issueName);
        //label.setIcon(getIssueIcon(auditIssue));
        return label;
    }


    private static JLabel topURL(Finding finding)
    {
        JLabel label = new JLabel(finding.baseURL);
        return label;
    }


    private static JLabel severity(Finding finding)
    {
        JLabel label = new JLabel(Finding.capitalize(finding.severity));
        label.setIcon(getIssueIcon(finding));
        return label;
    }

    private static JLabel confidence(Finding finding)
    {
        JLabel label = new JLabel(Finding.capitalize(finding.confidence));
        //label.setIcon(getIssueIcon(finding));
        return label;
    }

    private static JLabel getURL(Finding finding)
    {
        String final_URL="";
        for(String tmpurl:finding.URL) {
            final_URL=final_URL.concat(tmpurl+", ");
        }
        final_URL=final_URL.substring(0, final_URL.length() - 2);
        //final_URL=final_URL.concat("</html>");
        JLabel label = new JLabel(final_URL);
        //return final_URL;
        return label;
    }

    private static BurpIcon getIssueIcon(Finding finding)
    {
        return icon(getIssueIconFile(finding)).fontSized().build();
    }



    private static BurpIconFile getIssueIconFile(Finding finding)
    {
        BurpIconFile result;
        switch (finding.severity) {
            case "HIGH":
                switch (finding.confidence) {
                    case "CERTAIN":
                        result = SCAN_ISSUE_HIGH_CERTAIN;
                        break;
                    case "FIRM":
                        result = SCAN_ISSUE_HIGH_FIRM;
                        break;
                    case "TENTATIVE":
                        result = SCAN_ISSUE_HIGH_TENTATIVE;
                        break;
                    default:
                        result = null;
                }
                break;

            case "MEDIUM":
                switch (finding.confidence) {
                    case "CERTAIN":
                        result = SCAN_ISSUE_MEDIUM_CERTAIN;
                        break;
                    case "FIRM":
                        result = SCAN_ISSUE_MEDIUM_FIRM;
                        break;
                    case "TENTATIVE":
                        result = SCAN_ISSUE_MEDIUM_TENTATIVE;
                        break;
                    default:
                        result = null;
                }
                break;

            case "LOW":
                switch (finding.confidence) {
                    case "CERTAIN":
                        result = SCAN_ISSUE_LOW_CERTAIN;
                        break;
                    case "FIRM":
                        result = SCAN_ISSUE_LOW_FIRM;
                        break;
                    case "TENTATIVE":
                        result = SCAN_ISSUE_LOW_TENTATIVE;
                        break;
                    default:
                        result = null;
                }
                break;

            case "INFORMATION":
                switch (finding.confidence) {
                    case "CERTAIN":
                        result = SCAN_ISSUE_INFO_CERTAIN;
                        break;
                    case "FIRM":
                        result = SCAN_ISSUE_INFO_FIRM;
                        break;
                    case "TENTATIVE":
                        result = SCAN_ISSUE_INFO_TENTATIVE;
                        break;
                    default:
                        result = null;
                }
                break;

            case "FALSE_POSITIVE":
                result = SCAN_ISSUE_FALSE_POSITIVE;
                break;

            default:
                result = null;
        }
        return result;
    }





}

