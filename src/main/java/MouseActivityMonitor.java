import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MouseActivityMonitor extends JFrame {
    private static final long serialVersionUID = 1L;
    private JButton startButton;
    private JButton stopButton;
    private JTextArea logTextArea;
    private JComboBox<String> intervalComboBox;
    private Timer movementTimer;
    private boolean isRunning = false;
    private long intervalMillis = 60000; // Tempo padrão de 1 minuto em milissegundos

    public MouseActivityMonitor() {
        setTitle("Mouse Activity Monitor");
        setSize(410, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false); // Desabilita o redimensionamento
        setExtendedState(JFrame.NORMAL); // Impede a maximização

        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logTextArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        startButton = new JButton("Iniciar");
        stopButton = new JButton("Parar");
        stopButton.setEnabled(false);

        String[] intervals = {"30 segundos", "1 minuto", "2 minutos", "5 minutos", "10 minutos"};
        intervalComboBox = new JComboBox<>(intervals);
        intervalComboBox.setSelectedIndex(1); // 1 minuto selecionado por padrão

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startMouseMovement();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopMouseMovement();
            }
        });

        intervalComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateInterval();
            }
        });

        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(intervalComboBox);
        add(controlPanel, BorderLayout.SOUTH);

        // Inicializa o logger
        updateLogger("Aplicação iniciada!");
    }

    private void startMouseMovement() {
        if (!isRunning) {
            isRunning = true;
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            intervalComboBox.setEnabled(false);

            // Inicializa o Timer para executar o movimento do mouse a cada intervalo selecionado
            movementTimer = new Timer((int) intervalMillis, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moveMouseInPlus();
                }
            });
            movementTimer.start();

            // Realiza o primeiro movimento imediatamente
            this.moveMouseInPlus();
        }
    }

    private void stopMouseMovement() {
        if (isRunning) {
            isRunning = false;
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            intervalComboBox.setEnabled(true); // Habilita o ComboBox

            if (movementTimer != null) {
                movementTimer.stop();
            }

            this.updateLogger("Movimento do mouse interrompido.");
        }
    }

    private void moveMouseInPlus() {
        try {
            Robot robot = new Robot();
            Point originalLocation = MouseInfo.getPointerInfo().getLocation();

            // Movimento em forma de "+"
            int moveAmount = 10; // Distância do movimento do mouse
            int moveSpeed = 250; // Velocidade entre os movimentos

            // Move para cima
            robot.mouseMove(originalLocation.x, originalLocation.y - moveAmount);
            Thread.sleep(moveSpeed);
            // Move para baixo
            robot.mouseMove(originalLocation.x, originalLocation.y + 2 * moveAmount);
            Thread.sleep(moveSpeed);
            // Move para a esquerda
            robot.mouseMove(originalLocation.x - moveAmount, originalLocation.y);
            Thread.sleep(moveSpeed);
            // Move para a direita
            robot.mouseMove(originalLocation.x + 2 * moveAmount, originalLocation.y);
            Thread.sleep(moveSpeed);

            // Retorna à posição original
            robot.mouseMove(originalLocation.x, originalLocation.y);

            // Atualiza o logger com o horário do movimento e o próximo movimento
            this.updateLogger("Mouse movido às: " + getCurrentTime() + ". Próximo movimento em: " + getNextMovementTime());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLogger(String message) {
        SwingUtilities.invokeLater(() -> {
            logTextArea.append(message + "\n");
            logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
        });
    }

    private void updateInterval() {
        String selectedInterval = (String) intervalComboBox.getSelectedItem();
        switch (selectedInterval) {
            case "30 segundos":
                intervalMillis = 30000;
                break;
            case "1 minuto":
                intervalMillis = 60000;
                break;
            case "2 minutos":
                intervalMillis = 120000;
                break;
            case "5 minutos":
                intervalMillis = 300000;
                break;
            case "10 minutos":
                intervalMillis = 600000;
                break;
        }
        if (movementTimer != null && isRunning) {
            movementTimer.setDelay((int) intervalMillis);
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    private String getNextMovementTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date nextMoveDate = new Date(System.currentTimeMillis() + intervalMillis);
        return sdf.format(nextMoveDate);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MouseActivityMonitor frame = new MouseActivityMonitor();
            frame.setVisible(true);
        });
    }
}
