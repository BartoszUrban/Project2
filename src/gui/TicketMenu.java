package gui;

import controller.EmployeeController;
import controller.InquiryController;
import controller.ResponseController;
import controller.TicketController;
import db.DataAccessException;
import model.Employee;
import model.Inquiry;
import model.Response;
import model.Ticket;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TicketMenu extends JFrame implements Updatable {

    /**
     * Instance variables
     */
    private static final long serialVersionUID = 1L;
    private JPanel mainPanel;
    private JPanel ticketPanel;
    private JPanel customerPanel;
    private JPanel employeePanel;
    private JPanel conversationPanel;
    private JPanel createResponsePanel;
    private JPanel createInquiryPanel;
    private JScrollPane scrollPane;

    private JLabel lblTicket;
    private JLabel lblTicketID;
    private JLabel lblStartDate;
    private JLabel lblEndDate;
    private JLabel lblStatus;
    private JLabel lblPriority;

    private JComboBox<String> comboBoxTicketStatus;
    private JComboBox<String> comboBoxTicketPriority;

    private JLabel lblCustomer;
    private JLabel lblCustomerName;
    private JLabel lblCustomerPhone;
    private JLabel lblCustomerEmail;
    private JLabel lblCustomerType;
    private JLabel lblCustomerCompany;

    private JComboBox<String> comboBoxCustomerType;

    private JLabel lblEmployee;
    private JLabel lblEmployeeName;
    private JLabel lblEmployeePhone;
    private JLabel lblEmployeeEmail;
    private JLabel lblEmployeeType;
    private JLabel lblEmployeeDepartment;

    private JLabel lblInquiryTitle;
    private JLabel lblInquiryDescription;
    private JTextField txtInquiryTitle;
    private JTextField txtInquiryDescription;
    private JButton sendInquiryButton;

    private JLabel lblResponseTitle;
    private JLabel lblResponseDescription;
    private JTextField txtResponseTitle;
    private JTextField txtResponseDescription;
    private JButton sendResponseButton;

    private JComboBox<String> comboBoxEmployees;
    private List<JPanel> conversationsList;

    private TicketController ticketController;
    private InquiryController inquiryController;
    private ResponseController responseController;
    private EmployeeController employeeController;

    private Ticket currentTicket;

    /**
     * Launch the application.
     */

    public static void start(Ticket ticket) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    // ticket.setEndDate(ticket.getStartDate());
                    TicketMenu frame = new TicketMenu(ticket);
                    LoginMenu.setCurrentMenu(frame);
                    frame.setTitle("Ticket Menu");
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean checkForUpdates() {
        boolean updateFound = false;
        try {
            if (ticketController.checkForUpdates()) {
                updateFound = true;
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        return updateFound;
    }

    @Override
    public void updateMenu() {
        Object[] options = {"Yes", "Remind me in 2 minutes"};
        int dialogResult = JOptionPane.showOptionDialog(rootPane, "Update found, update?", "Update found", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (dialogResult == 0) {
            System.out.println("Updating...");

            try {
                currentTicket = ticketController.findTicketByID(currentTicket.getTicketID(), true);
            } catch (DataAccessException e) {
                e.printStackTrace();
            }

            createComponents(currentTicket);
            createGUI();
            LoginMenu.addDelayTime(2 * 60 * 1000);
        } else {
            LoginMenu.addDelayTime(2 * 60 * 1000);
        }
    }

    /**
     * Create the frame.
     */
    public TicketMenu(Ticket ticket) {
        ticketController = new TicketController();
        inquiryController = new InquiryController();
        responseController = new ResponseController();
        employeeController = new EmployeeController();

        currentTicket = ticket;
        createComponents(currentTicket);
        createGUI();
    }

    private void createComponents(Ticket ticket) {
        conversationsList = new ArrayList<>();
        mainPanel = new JPanel();
        ticketPanel = new JPanel();
        customerPanel = new JPanel();
        employeePanel = new JPanel();
        conversationPanel = new JPanel();
        createInquiryPanel = new JPanel();
        createResponsePanel = new JPanel();
        scrollPane = new JScrollPane(conversationPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        lblTicket = new JLabel("Ticket");
        lblTicketID = new JLabel("Ticket ID:" + ticket.getTicketID());
        lblStartDate = new JLabel("Start Date: " + ticket.getStartDate());
        lblEndDate = new JLabel("End Date: " + ticket.getEndDate());
        lblStatus = new JLabel("Ticket Status: ");
        lblPriority = new JLabel("Ticket Priority: ");

        ticket.getInquiries().forEach(e -> getInquiryPanel(e));

        comboBoxTicketStatus = new JComboBox<String>(Arrays.asList(Ticket.TicketComplaintStatus.values()).stream().map(e -> e.getTitle()).collect(Collectors.toCollection(ArrayList::new)).toArray(String[]::new));
        comboBoxTicketStatus.setSelectedIndex(ticket.getComplaintStatus().ordinal());
        comboBoxTicketStatus.addItemListener(e -> {
            ticket.setComplaintStatus(comboBoxTicketStatus.getSelectedItem().toString());
            updateTicket(ticket);
        });

        comboBoxTicketPriority = new JComboBox<String>(Arrays.asList(Ticket.Priority.values()).stream().map(e -> e.getTitle()).collect(Collectors.toCollection(ArrayList::new)).toArray(String[]::new));
        comboBoxTicketPriority.setSelectedIndex(ticket.getPriority().ordinal());
        comboBoxTicketPriority.addItemListener(e -> {
            ticket.setPriority(comboBoxTicketPriority.getSelectedItem().toString());
            updateTicket(ticket);
        });

        lblCustomer = new JLabel("Customer");
        lblCustomerName = new JLabel("Name: ");
        lblCustomerPhone = new JLabel("Phone: ");
        lblCustomerEmail = new JLabel("Email: ");
        lblCustomerType = new JLabel("Type: ");
        lblCustomerCompany = new JLabel("Company: ");

        if (ticket.getCustomer() != null) {
            lblCustomerName
                    .setText("Name: " + ticket.getCustomer().getFirstName() + " " + ticket.getCustomer().getLastName());
            lblCustomerPhone.setText("Phone: " + ticket.getCustomer().getPhoneNumber());
            lblCustomerEmail.setText("Email: " + ticket.getCustomer().getEmail());
            lblCustomerType.setText("Type: " + ticket.getCustomer().getCustomerType().getLabel());
            lblCustomerCompany.setText("Company: " + ticket.getCustomer().getCompanyName());
        }

        lblEmployee = new JLabel("Employee");
        lblEmployeeName = new JLabel("Name: ");
        lblEmployeePhone = new JLabel("Phone: ");
        lblEmployeeEmail = new JLabel("Email: ");
        lblEmployeeType = new JLabel("Type: ");
        lblEmployeeDepartment = new JLabel("Department: ");

        if (ticket.getEmployee() != null) {
            setEmployeeLabels(ticket);
        }

        lblInquiryTitle = new JLabel("Inquiry Title: ");
        lblInquiryDescription = new JLabel("Inquiry Description: ");
        txtInquiryTitle = new JTextField(20);
        txtInquiryDescription = new JTextField(20);
        sendInquiryButton = new JButton("Send Inquiry");
        sendInquiryButton.addActionListener(e -> {
            createInquiry(ticket);
            dispose();
            TicketMenu.start(ticket);
        });

        lblResponseTitle = new JLabel("Response Title: ");
        lblResponseDescription = new JLabel("Response Description: ");
        txtResponseTitle = new JTextField(20);
        txtResponseDescription = new JTextField(20);
        sendResponseButton = new JButton("Send Response");
        sendResponseButton.addActionListener(e -> {
            createResponse(ticket);
            dispose();
            TicketMenu.start(ticket);
        });

        try {
            comboBoxEmployees = new JComboBox<String>(employeeController.getAllEmployees(false).stream().map(e -> e.getEmail()).toArray(String[]::new));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        comboBoxEmployees.setToolTipText("Assigned to:");
        comboBoxEmployees.setSelectedIndex(ticket.getEmployee() != null ? ticket.getEmployee().getPersonID() - 1 : 0);
        comboBoxEmployees.addItemListener(e -> {
            try {
                Employee employee = employeeController.findEmployeeByEmail(comboBoxEmployees.getSelectedItem().toString(), false);
                ticket.setEmployee(employee);
                updateTicket(ticket);
                setEmployeeLabels(ticket);
                employeeCheck(ticket);
            } catch (DataAccessException dataAccessException) {
                dataAccessException.printStackTrace();
            }
        });
    }

    private void setWindowSizeAndLocation() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth() / 1.5;
        double height = screenSize.getHeight() / 1.5;
        screenSize.setSize(width, height);

        setPreferredSize(screenSize);
        setLocation((int) width / 4, (int) height / 4);
    }

    private void createGUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(mainPanel);
        setWindowSizeAndLocation();

        mainPanel.setLayout(new MigLayout());
        ticketPanel.setLayout(new MigLayout());
        customerPanel.setLayout(new MigLayout());
        employeePanel.setLayout(new MigLayout());
        conversationPanel.setLayout(new MigLayout());
        createInquiryPanel.setLayout(new MigLayout());
        createResponsePanel.setLayout(new MigLayout());

        mainPanel.add(ticketPanel, "split 3");
        mainPanel.add(customerPanel, "gapleft 75");
        mainPanel.add(employeePanel, "wrap, gapleft 75");
        mainPanel.add(scrollPane, "wrap");
        mainPanel.add(createInquiryPanel, "split 2");
        mainPanel.add(createResponsePanel, "");

        conversationsList.forEach(e -> conversationPanel.add(e, "wrap"));

        ticketPanel.add(lblTicket, "wrap");
        ticketPanel.add(lblTicketID, "wrap");
        ticketPanel.add(lblStartDate, "wrap");
        ticketPanel.add(lblEndDate, "wrap");
        ticketPanel.add(lblStatus, "split 2");
        ticketPanel.add(comboBoxTicketStatus, "wrap");
        ticketPanel.add(lblPriority, "split 2");
        ticketPanel.add(comboBoxTicketPriority, "wrap");

        customerPanel.add(lblCustomer, "wrap");
        customerPanel.add(lblCustomerName, "wrap");
        customerPanel.add(lblCustomerPhone, "wrap");
        customerPanel.add(lblCustomerEmail, "wrap");
        customerPanel.add(lblCustomerType, "wrap");
        customerPanel.add(lblCustomerCompany, "wrap");

        employeePanel.add(lblEmployee, "wrap");
        employeePanel.add(lblEmployeeName, "wrap");
        employeePanel.add(lblEmployeePhone, "wrap");
        employeePanel.add(lblEmployeeEmail, "wrap");
        employeePanel.add(lblEmployeeType, "wrap");
        employeePanel.add(lblEmployeeDepartment, "wrap");
        employeePanel.add(comboBoxEmployees, "wrap");

        createInquiryPanel.add(lblInquiryTitle);
        createInquiryPanel.add(txtInquiryTitle, "wrap");
        createInquiryPanel.add(lblInquiryDescription);
        createInquiryPanel.add(txtInquiryDescription, "wrap");
        createInquiryPanel.add(sendInquiryButton);

        createResponsePanel.add(lblResponseTitle);
        createResponsePanel.add(txtResponseTitle, "wrap");
        createResponsePanel.add(lblResponseDescription);
        createResponsePanel.add(txtResponseDescription, "wrap");
        createResponsePanel.add(sendResponseButton);

        pack();
    }

    private void getInquiryPanel(Inquiry inquiry) {
        JPanel inquiryPanel = new JPanel();
        inquiryPanel.setLayout(new MigLayout());
        conversationsList.add(inquiryPanel);

        JLabel lblInquiry = new JLabel("Inquiry");
        JLabel lblCategory = new JLabel("Category: " + inquiry.getCategory().getLabel());
        JLabel lblTimestamp = new JLabel("Timestamp: " + inquiry.getTimestamp());
        JLabel lblTitle = new JLabel("Title: " + inquiry.getTitle());
        JLabel lblDescription = new JLabel("Description: " + inquiry.getDescription());

        inquiryPanel.add(lblInquiry);
        inquiryPanel.add(lblTimestamp);
        inquiryPanel.add(lblCategory, "wrap");
        inquiryPanel.add(lblTitle, "wrap");
        inquiryPanel.add(lblDescription, "wrap");

        inquiryPanel.setBorder(BorderFactory.createLineBorder(Color.blue));

        try {
            inquiry.setResponses(new InquiryController().findInquiryByID(inquiry.getInquiryID(), true).getResponses());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        inquiry.getResponses().forEach(e -> getResponsePanel(e));
    }

    private void getResponsePanel(Response response) {
        JPanel responsePanel = new JPanel();
        responsePanel.setLayout(new MigLayout());
        conversationsList.add(responsePanel);

        JLabel lblResponse = new JLabel("Response");
        if (response.getEmployee() != null)
            lblResponse.setText("Response ... " + response.getEmployee().getFullName() + " (" + response.getEmployee().getEmployeeType().getLabel() + ")");

        JLabel lblTimestamp = new JLabel("Timestamp: " + response.getTimestamp());
        JLabel lblTitle = new JLabel("Title: " + response.getTitle());
        JLabel lblDescription = new JLabel("Description: " + response.getDescription());

        responsePanel.add(lblResponse);
        responsePanel.add(lblTimestamp, "wrap");
        responsePanel.add(lblTitle, "wrap");
        responsePanel.add(lblDescription, "wrap");

        responsePanel.setBorder(BorderFactory.createLineBorder(Color.red));
    }

    private void createInquiry(Ticket ticket) {
        Inquiry inquiry = new Inquiry();
        inquiry.setTitle(txtInquiryTitle.getText()).setDescription(txtInquiryDescription.getText())
                .setTimestamp(LocalDateTime.now());
        inquiryController = new InquiryController();
        try {
            if (inquiryController.createInquiry(inquiry, ticket.getTicketID()))
                ticket.addInquiry(inquiry);
        } catch (DataAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void createResponse(Ticket ticket) {
        Response response = new Response();
        response.setTitle(txtResponseTitle.getText()).setDescription(txtResponseDescription.getText())
                .setTimestamp(LocalDateTime.now()).setEmployee(LoginMenu.getLoggedEmployee());
        responseController = new ResponseController();
        Inquiry inquiry = ticket.getInquiries().get(ticket.getInquiries().size() - 1);

        try {
            if (responseController.createResponse(response, inquiry.getInquiryID())) {
                inquiry.addResponse(response);
            }
        } catch (DataAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void updateTicket(Ticket ticket) {
        try {
            ticketController.updateTicket(ticket,
                    ticket.getComplaintStatus().getTitle(),
                    ticket.getPriority().getTitle(),
                    ticket.getStartDate(),
                    ticket.getEndDate(),
                    ticket.getEmployee(),
                    ticket.getCustomer());
        } catch (DataAccessException dataAccessException) {
            dataAccessException.printStackTrace();
        }
    }

    private void setEmployeeLabels(Ticket ticket) {
        lblEmployeeName.setText("Name: " + ticket.getEmployee().getFirstName() + " " + ticket.getEmployee().getLastName());
        lblEmployeePhone.setText("Phone: " + ticket.getEmployee().getPhoneNumber());
        lblEmployeeEmail.setText("Email: " + ticket.getEmployee().getEmail());
        lblEmployeeType.setText("Type: " + ticket.getEmployee().getEmployeeType().getLabel());
        try {
            ticket.setEmployee(employeeController.findEmployeeByID(ticket.getEmployee().getPersonID(), true));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        lblEmployeeDepartment.setText("Department: " + ticket.getEmployee().getDepartment().getName());
    }

    private void employeeCheck(Ticket ticket) {
        if (ticket.getEmployee() == null) {
            comboBoxTicketStatus.setEnabled(false);
            comboBoxTicketPriority.setEnabled(false);
            sendResponseButton.setEnabled(false);
        } else {
            comboBoxTicketStatus.setEnabled(true);
            comboBoxTicketPriority.setEnabled(true);
            sendResponseButton.setEnabled(true);
        }
    }
}