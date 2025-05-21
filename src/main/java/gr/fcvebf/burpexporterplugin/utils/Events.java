package gr.fcvebf.burpexporterplugin.utils;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.scanner.audit.issues.AuditIssue;
import gr.fcvebf.burpexporterplugin.models.Finding;
import gr.fcvebf.burpexporterplugin.models.pwndoc.AuditType;
import gr.fcvebf.burpexporterplugin.utils.Config.exportOptionsEnum;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

public class Events
{
    public interface Event
    {

    }

    public interface EventListener<E extends Event>
    {
        void onEvent(E event);
    }

    private static final Map<Class<?>, List<EventListener<?>>> subscriptions = new HashMap<>();

    public static <E extends Event> void subscribe(Class<E> eventType, EventListener<E> listener)
    {
        subscriptions.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public static <E extends Event> void publish(E event)
    {
        List<EventListener<?>> listeners = subscriptions.getOrDefault(event.getClass(), emptyList());
        for (EventListener<?> listener : listeners)
        {
            //noinspection unchecked
            ((EventListener<E>) listener).onEvent(event);
        }
    }

    public record AddIssuesEvent(List<AuditIssue> auditIssues) implements Event
    {
    }

    public record RemoveSelectedIssues() implements Event
    {
    }

    public record RemoveAllIssues() implements Event
    {
    }


    public record ExportFindingsClick(List<Finding> auditFindings) implements Event
    {
    }



    public record ExportIssuesEvent(List<Finding> auditFindings, exportOptionsEnum selectedOption,MontoyaApi montoyaapi, PwndocOptions pwndocOptions, CSVOptions csvOptions, JSONOptions jsonOptions, XMLOptions XMLOptions, MarkdownOptions markdownOptions, DocxOptions docxOptions) implements Event
    {
    }

    public record IssuesSelectedEvent(int[] selectedRows) implements Event
    {
    }



    public record UpdateDebugEvent(String message) implements Event
    {
    }


    public record PwndocExportedEvent(String message) implements Event
    {
    }

    public record CSVExportedEvent(String message) implements Event
    {
    }

    public record JSONExportedEvent(String message) implements Event
    {
    }

    public record XMLExportedEvent(String message) implements Event
    {
    }

    public record MarkdownExportedEvent(String message) implements Event
    {
    }

    public record DocxExportedEvent(String message) implements Event
    {
    }


    public record PwndocLoginButtonEvent(String PwndocURL, String pwndocusername, String pwndocpass, JPanel pwndocAuditSelectionOptions,JPanel pwndocFindingsOptions, JComboBox<AuditType.ComboAuditTypeItem> cmb_PwndocAuditType,JComboBox cmb_PwndocLanguage,JComboBox cmb_PwndocFindingCategory,JComboBox cmb_PwndocFindingType,MontoyaApi montoyaApi) implements Event
    {
    }


}


