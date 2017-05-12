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

public class Main extends Super{
    static Main.GameDisplay gamePanel;   //draw the game components here

    private static class GameDisplay extends JPanel {

        @Override
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            //System.out.println("* Repaint *");

            if (gameOver) {
                graphics.drawString( "Game over!", 20, 30 );
                return;
            }

            if (removeInstructions ) {
                graphics.drawString("Pong! Press up or down to move", 20, 30);
                graphics.drawString("Press q to quit", 20, 60);
            }

            graphics.setColor(Color.blue);

            //While game is playing, these methods draw the ball, paddles, using the global variables
            //Other parts of the code will modify these variables

            //Ball - a circle is just an oval with the height equal to the width
            graphics.drawOval((int)ballX, (int)ballY, ballSize, ballSize);
            //Computer paddle
            graphics.drawLine(paddleDistanceFromSide, computerPaddleY - paddleSize, paddleDistanceFromSide, computerPaddleY + paddleSize);
            //Human paddle
            graphics.drawLine(screenSize - paddleDistanceFromSide, humanPaddleY - paddleSize, screenSize - paddleDistanceFromSide, humanPaddleY + paddleSize);

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

            removeInstructions = true;   //game has started

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
            if (humanPaddleY < screenSize - paddleSize) {
                humanPaddleY+=humanPaddleMaxSpeed;
            }
        }

        private void moveUp() {
            //Coordinates increase as you go down the screen, that's why this looks backwards.
            if (humanPaddleY > paddleSize) {
                humanPaddleY-=humanPaddleMaxSpeed;
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
        window.setSize(screenSize, screenSize);
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

        timer = new Timer(75, gameUpdater);
        timer.start();    //Every time the timer ticks, the actionPerformed method of the ActionListener is called
    }

    //Uses the current position of ball and paddle to move the computer paddle towards the ball
    protected static void moveComputerPaddle(){

        int ballPaddleDifference = computerPaddleY - (int)ballY;
        int distanceToMove = Math.min(Math.abs(ballPaddleDifference), computerPaddleMaxSpeed);

        if (ballPaddleDifference > 0 ) {   //Difference is positive - paddle is below ball on screen
            computerPaddleY -= distanceToMove;

        } else if (ballPaddleDifference < 0){
            computerPaddleY += distanceToMove;

        } else {
            //Ball and paddle are aligned. Don't need to move!
            computerPaddleSpeed = 0;
        }

    }

    //Checks to see if the ball has hit a wall or paddle
    //If so, bounce off the wall/paddle
    //And then move ball in the correct direction
    protected static void moveBall() {
        ballX = ballX + (ballSpeed * Math.cos(ballDirection));
        ballY = ballY + (ballSpeed * Math.sin(ballDirection));
    }

    private static void checkDirectionChange(){
        if (checkHitHumanPaddle()) {
            ballDirection = (Math.PI) - ballDirection;
        }
        if (checkHitComputePaddle()) {
            ballDirection = (Math.PI) - ballDirection;
        }
        if(checkBallHitWall()){
            ballDirection = (2 * Math.PI) - ballDirection;
        }
    }

    private static boolean checkGameOver(){
        if (ballX <= 0 || ballX >= screenSize ) {
            gameOver = true;
            timer.stop();
            return true;
        }
        return false;
    }

    private static boolean checkBallHitWall(){
        if (ballY <= 0 || ballY >= screenSize-ballSize) {
            return true;
        }
        return false;
    }

    private static boolean checkHitHumanPaddle(){
        if (ballX >= screenSize-(paddleDistanceFromSide+(ballSize)) && (ballY > humanPaddleY-paddleSize && ballY < humanPaddleY+paddleSize)){
            return true;
        }
        return false;
    }

    private static boolean checkHitComputePaddle(){
        if (ballX <= paddleDistanceFromSide && (ballY > computerPaddleY-paddleSize && ballY < computerPaddleY+paddleSize)){
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
