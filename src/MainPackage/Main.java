package MainPackage; /**
 * Created by arko1 on 07/03/2017.
 */
import ValuesPackage.Values;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


//TODO have paddle speed affect ball's direction
//TODO known issue - sometimes ball gets stuck behind human paddle

public class Main {
    static Main.GameDisplay gamePanel;   //draw the game components here
    private final Super aSuper = new Super();

    private static class GameDisplay extends JPanel {

        @Override
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            //System.out.println("* Repaint *");

            if (Super.gameOver) {
                graphics.drawString( "Game over!", 20, 30 );
                return;
            }

            if (Super.removeInstructions) {
                graphics.drawString("Pong! Press up or down to move", 20, 30);
                graphics.drawString("Press q to quit", 20, 60);
            }

            graphics.setColor(Color.blue);

            //While game is playing, these methods draw the ball, paddles, using the global variables
            //Other parts of the code will modify these variables

            //Ball - a circle is just an oval with the height equal to the width
            graphics.drawOval((int) Super.ballX, (int) Super.ballY, Super.ballSize, Super.ballSize);
            //Computer paddle
            graphics.drawLine(Super.paddleDistanceFromSide, Super.computerPaddleY - Super.paddleSize, Super.paddleDistanceFromSide, Super.computerPaddleY + Super.paddleSize);
            //Human paddle
            graphics.drawLine(Super.screenSize - Super.paddleDistanceFromSide, Super.humanPaddleY - Super.paddleSize, Super.screenSize - Super.paddleDistanceFromSide, Super.humanPaddleY + Super.paddleSize);

        }
    }

    //Listen for user pressing a key, and moving human paddle in response
    private static class KeyHandler implements KeyListener {

        @Override
        public void keyTyped(KeyEvent keyEvent) {
            char keyPressed = keyEvent.getKeyChar();
            char qiuteKey = 'q';
            if( keyPressed == qiuteKey){
                System.exit(0);    //quit if user presses the q key.
            }
        }

        @Override
        public void keyReleased(KeyEvent ev) {}   //Don't need this one, but required to implement it.

        @Override
        public void keyPressed(KeyEvent ev) {

            Super.removeInstructions = true;   //game has started

            if (ev.getKeyCode() == KeyEvent.VK_DOWN) {
                System.out.println("down key");
                moveDown();
            }
            if (ev.getKeyCode() == KeyEvent.VK_UP) {
                System.out.println("up key");
                moveUp();
            }

            //ev.getComponent() returns the GUI component that generated this event
            //In this case, it will be GameDisplay JPanel
            ev.getComponent().repaint();   //This calls paintComponent(Graphics g) again
        }

        private void moveDown() {
            //Coordinates decrease as you go up the screen, that's why this looks backwards.
            if (Super.humanPaddleY < Super.screenSize - Super.paddleSize) {
                Super.humanPaddleY += Super.humanPaddleMaxSpeed;
            }
        }

        private void moveUp() {
            //Coordinates increase as you go down the screen, that's why this looks backwards.
            if (Super.humanPaddleY > Super.paddleSize) {
                Super.humanPaddleY -= Super.humanPaddleMaxSpeed;
            }
        }

    }


    public static void main(String[] args) {

        gamePanel = new GameDisplay();

        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(gamePanel, BorderLayout.CENTER);

        JFrame window = new JFrame();
        window.setUndecorated(true);   //Hides the title bar.

        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);   //Quit the program when we close this window
        window.setContentPane(content);
        window.setSize(Super.screenSize, Super.screenSize);
        window.setLocation(100,100);    //Where on the screen will this window appear?
        window.setVisible(true);

        KeyHandler listener = new KeyHandler();
        window.addKeyListener(listener);

        //Below, we'll create and start a timer that notifies an ActionListener every time it ticks
        //First, need to create the listener:
        ActionListener gameUpdater = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //gameUpdater is an inner class
                //It's containing class is MainPackage.Main
                //moveBall() and moveComputerPaddle belong to the outer class - MainPackage.Main
                //So we have to say MainPackage.Main.moveBall() to refer to these methods
                if(!Main.checkGameOver()) {
                    Main.checkDirectionChange();
                    Main.moveBall();
                    Main.moveComputerPaddle();
                }

                gamePanel.repaint();
            }
        };

        Super.timer = new Timer(75, gameUpdater);
        Super.timer.start();    //Every time the timer ticks, the actionPerformed method of the ActionListener is called
    }

    //Uses the current position of ball and paddle to move the computer paddle towards the ball
    protected static void moveComputerPaddle(){

        int ballPaddleDifference = Super.computerPaddleY - (int) Super.ballY;
        int distanceToMove = Math.min(Math.abs(ballPaddleDifference), Super.computerPaddleMaxSpeed);

        if (ballPaddleDifference > 0 ) {   //Difference is positive - paddle is below ball on screen
            Super.computerPaddleY -= distanceToMove;

        } else if (ballPaddleDifference < 0){
            Super.computerPaddleY += distanceToMove;

        } else {
            //Ball and paddle are aligned. Don't need to move!
            Super.computerPaddleSpeed = 0;
        }

    }

    //Checks to see if the ball has hit a wall or paddle
    //If so, bounce off the wall/paddle
    //And then move ball in the correct direction
    protected static void moveBall() {
        Super.ballX = Super.ballX + (Super.ballSpeed * Math.cos(Super.ballDirection));
        Super.ballY = Super.ballY + (Super.ballSpeed * Math.sin(Super.ballDirection));
    }

    private static void checkDirectionChange(){
        if (checkHitHumanPaddle()) {
            Super.ballDirection = (Math.PI) - Super.ballDirection;
        }
        if (checkHitComputePaddle()) {
            Super.ballDirection = (Math.PI) - Super.ballDirection;
        }
        if(checkBallHitWall()){
            Super.ballDirection = (2 * Math.PI) - Super.ballDirection;
        }
    }

    private static boolean checkGameOver(){
        if (Super.ballX <= 0 || Super.ballX >= Super.screenSize) {
            Super.gameOver = true;
            Super.timer.stop();
            return true;
        }
        return false;
    }

    private static boolean checkBallHitWall(){
        if (Super.ballY <= 0 || Super.ballY >= Super.screenSize - Super.ballSize) {
            return true;
        }
        return false;
    }

    private static boolean checkHitHumanPaddle(){
        if (Super.ballX >= Super.screenSize -(Super.paddleDistanceFromSide +(Super.ballSize)) && (Super.ballY > Super.humanPaddleY - Super.paddleSize && Super.ballY < Super.humanPaddleY + Super.paddleSize)){
            return true;
        }
        return false;
    }

    private static boolean checkHitComputePaddle(){
        if (Super.ballX <= Super.paddleDistanceFromSide && (Super.ballY > Super.computerPaddleY - Super.paddleSize && Super.ballY < Super.computerPaddleY + Super.paddleSize)){
            return true;
        }
        return false;
    }
    private Values values = new ValuesPackage.ValuesBuilder().setA(1).setB(2).setC(3).createValues();
    private int MethodToDoExtractMethod(){
        return Add(values.getA(), values.getB(), values.getC());
    }

    private int Add(int a, int b, int c) {
        return new Add(a, b, c).invoke();
    }

    public int getRandomInt(){
        return (int)Math.random();
    }

    private class Add {
        private int a;
        private int b;
        private int c;

        public Add(int a, int b, int c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public int invoke() {
            a = a + b;
            a = a + c;
            return a;
        }
    }
}
