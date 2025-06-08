---
title: "Security Findings Report"
author: "BurpSuite Issue Exporter"
date: "${.now?string("yyyy-MM-dd HH:mm:ss")}"
---


# 🛡️ Security Assessment Report

<div style="text-align: center;">
  ${projectname}
</div>

<div style="page-break-after: always;"></div>


## Document Properties
<table>
  <tr><th colspan="2" class="header">Report Details</th></tr>
  <tr><th>Property</th><th>Value</th></tr>
  <tr><td>Title</td><td>${projectname}</td></tr>
  <tr><td>Category</td><td>Security Assessment Report</td></tr>
  <tr><td>Classification</td><td><ul style="list-style: none; padding-left: 0; font-family: sans-serif;">
                                   <li>○ Top Secret</li>
                                   <li>○ Secret</li>
                                   <li>● Confidential</li>
                                   <li>○ Internal</li>
                                   <li>○ Public</li>
                                 </ul></td></tr>
  <tr><td>Latest Version Date</td><td>${projectDate}</td></tr>
</table>
<div style="page-break-after: always;"></div>

## Statement of Confidentiality
This document contains confidential information intended solely for the recipient organization. The contents of this report, including all findings, recommendations, and supporting data, are provided exclusively for the purposes of evaluating and improving the organization’s security posture. Unauthorized distribution, reproduction, or disclosure of any part of this document is strictly prohibited without prior written consent.

<div style="page-break-after: always;"></div>

## 📑 Table of Contents

<#list findings as finding>
- [${finding.issueName}](#${finding.issueName?replace(" ", "-")?lower_case})
</#list>
<div style="page-break-after: always;"></div>

## Executive Summary

${execSummary}


## 📘 Findings Summary

The following table presents the identified security findings resulting from the assessment. Each finding includes a brief description, severity rating, and relevant details to support risk evaluation and remediation efforts. These findings are intended to provide actionable insights to improve the organization’s overall security posture.

| Issue Name | Severity |
|------------|----------|
<#list findings as finding>
| ${finding.issueName} | ${finding.severity} |
</#list>

<div style="page-break-after: always;"></div>
<#list findings as finding>

## ${finding.issueName}
- **Severity**: ${finding.severity}
- **Target**: ${finding.baseURL}
- **Background**:
  ${(finding.issueDescrBackground!"")?replace("\n", "\n  ")}

- **Issue Details**:
  ${(finding.issueDetailsSummaryHTML!"")?replace("\n", "\n  ")}

<#if includeHttpRequests == true && finding.HTTPPOC?? && finding.HTTPPOC?has_content>
- **HTTP Requests**:
  <#list finding.HTTPPOC as poc>
   - **HTTP Request**:
<pre>
<code>
${poc[0]?html}
</code>
</pre>

   - **HTTP Response**:
<pre>
<code>
${poc[1]?html}
</code>
</pre>

    <div style="page-break-after: always;"></div>
  </#list>
<#else>
</#if>

- **Recommendation**:
  ${finding.issueRemediation?replace("\n", "\n  ")}

<div style="page-break-after: always;"></div>
</#list>