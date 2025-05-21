## Exporter++ Burp Suite extension
This Burp Suite extension allows the user to upload the identified security issues directly to Pwndoc or export them in various document formats.

## Features
* Export options: Pwndoc, CSV, JSON, XML, Markdown and Docx.
* Option to include also the full HTTP Requests/Responses of the Security issues.
* Custom template support for Markdown (Apache FreeMarker) or Docx (XDocReport with Apache Velocity engine).
* Only the user-selected security issues are exported
* The Security issues are sorted by severity, confidence and name

## Usage
1. In the Target Tab, select the Issues you want to export
2. Right-click and select Extensions -> Exporter++ -> Send to Exporter++
   ![Send to Exporter](images/sendtoexporter.png "Send to Exporter")
3. Go to the Exporter++ tab and choose the appropriate export options you want
   * Pwndoc
     ![Upload to Pwndoc](./images/export_pwndoc.png "Upload to Pwndoc")
     And we see that our scan has been uploaded
     ![Pwndoc Audit](./images/export_pwndoc_poc.png "Pwndoc Audit")
   * CSV
     ![Export to CSV](./images/export_csv.png "Export to CSV")
   * JSON
     ![Export to JSON](./images/export_json.png "Export to JSON")
   * XML
     ![Export to XML](./images/export_xml.png "Export to XML")
   * Markdown
     ![Export to Markdown](./images/export_markdown.png "Export to Markdown")
   * Docx
     ![Export to Docx](./images/export_docx.png "Export to Docx")
4. Click the Export button to start the export process

