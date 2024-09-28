import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.random.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.lang.*;

import java.util.concurrent.ThreadLocalRandom;

public class Minesweeper {
    private class Tile extends JButton {
        int row;
        int col;
        int num;
        boolean revealed = false;
        boolean isMine = false;
        boolean isSafe = false;

        public Tile(int r, int c){
            this.row = r;
            this.col = c;
        }
        public boolean equals(Tile other){
            if(other.row == this.row && other.col == this.col){
                return true;
            }
            else{
                return false;
            }
        }
    }

    int tileSize = 70;
    int numRows = 8;
    int numMines = 10;
    int numFlags = 10;
    int numCols = numRows;
    int boardWidth = numCols * tileSize;
    int boardHeight = numRows * tileSize;

    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JLabel flagLabel = new JLabel();
    boolean firstClick = true;
    JButton restartButton = new JButton("Restart");
    JPanel restartPanel = new JPanel();

    Tile[][] board = new Tile[numRows][numCols];
    ArrayList<Tile> mines = new ArrayList<Tile>();

    Minesweeper() {
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                Minesweeper newGame = new Minesweeper();
            }
        });

        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper -- 🏁 " + Integer.toString(numFlags));
        textLabel.setOpaque(true);

        flagLabel.setFont(new Font("Arial", Font.BOLD, 12));
        flagLabel.setText("Flags : " + mines.size());
        flagLabel.setOpaque(true);

 
        restartPanel.setLayout(new BorderLayout());
        restartPanel.add(restartButton);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
     //  textPanel.add(flagLabel);
        frame.add(textPanel, BorderLayout.NORTH);


        boardPanel.setLayout(new GridLayout(numRows, numCols));
      //  boardPanel.setBackground(Color.GREEN);
        frame.add(boardPanel);

        for(int row = 0; row < numRows; row++){
            for(int col = 0; col < numCols; col++){
                Tile newTile = new Tile(row, col);
                board[row][col] = newTile;

                newTile.setFocusable(false);
                newTile.setMargin(new Insets(0, 0, 0, 0));
                newTile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
                newTile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e){
                        Tile tile = (Tile) e.getSource();

                        //LEFT
                        if(e.getButton() == MouseEvent.BUTTON1){
                            if(firstClick){
                                createSafeZone(tile.row, tile.col);
                                createMines();
                                createNumbers();
                               openZeros(tile.row, tile.col);
                                firstClick = false;
                            }
                            else{
                            if(tile.getText() == ""){
                                if(mines.contains(tile)){
                                    System.out.println("game over");
                                    showMines();
                                    textLabel.setText("GAME OVER!");
                                    frame.remove(boardPanel);
                                    frame.add(restartPanel, BorderLayout.CENTER);
                                    return;
                                }
                                else{
                                    tile.setText(String.valueOf(tile.num));
                                    if(tile.num == 0){
                                        openZeros(tile.row, tile.col);
                                    }
                                    tile.revealed = true;
                                }
                            }
                        }
                        }
                        else if(e.getButton() == MouseEvent.BUTTON3){
                            if(!tile.revealed && tile.getText() == ""){
                                plantFlag(tile);
                                numFlags--;
                                textLabel.setText("Minesweeper -- 🏁 " + Integer.toString(numFlags));
                                if(numMines == 0){
                                    textLabel.setText("Mines Defused! You Win!");
                                    frame.remove(boardPanel);
                                    frame.add(restartPanel, BorderLayout.CENTER);
                                    return;
                                }
                            }
                            else if(!tile.revealed && tile.getText() == "🏁"){
                                tile.setText("");
                                numFlags++;
                                textLabel.setText("Minesweeper -- 🏁 " + Integer.toString(numFlags));
                            }
                        }
                    }
                });
                boardPanel.add(newTile);
            }
        }
            frame.setVisible(true);
          // showMines();
         //   createNumbers();
    }
    private void createSafeZone(int row, int col){
        int topRow = row -1;
        int bottomRow = row + 1;
        board[row][col].isSafe = true;
        if(topRow >= 0){
            board[topRow][col].isSafe = true;
            board[topRow][col].setText("a");
            if(col - 1 >= 0){
                board[topRow][col - 1].isSafe = true;
                board[row][col - 1].isSafe = true;
           //     board[row][col - 1].setText("a");
            //    board[topRow][col - 1].setText("a");
            }
            if(col +1 < numCols){
                board[topRow][col + 1].isSafe = true;
                board[row][col + 1].isSafe = true;
             //   board[row][col + 1].setText("a");
            //    board[topRow][col + 1].setText("a");
            }
        }
        if(bottomRow < numRows){
            board[bottomRow][col].isSafe = true;
           // board[bottomRow][col].setText("a");
            if(col - 1 >= 0){
                board[bottomRow][col - 1].isSafe = true;
                board[row][col - 1].isSafe = true;
            //    board[row][col - 1].setText("a");
            //    board[bottomRow][col - 1].setText("a");
            }
            if(col +1 < numCols){
                board[bottomRow][col + 1].isSafe = true;
                board[row][col + 1].isSafe = true;
             //   board[row][col  1].setText("a");
               // board[bottomRow][col + 1].setText("a");
            }
        }
        
    }
    private void createMines(){
        int mineRow;
        int mineCol;
        for(int i = 0; i < numMines; i++){
            do{
             mineRow = ThreadLocalRandom.current().nextInt(0, numRows);
             mineCol = ThreadLocalRandom.current().nextInt(0, numCols);
            } while(board[mineRow][mineCol].isSafe || mines.contains(board[mineRow][mineCol]));
            board[mineRow][mineCol].isMine = true;
            System.out.println("Y:" + String.valueOf(mineRow) +", X:" + String.valueOf(mineCol));
            mines.add(board[mineRow][mineCol]);
        }
    }
    private void showMines(){
        for(int row = 0; row < numRows; row++){
            for(int col = 0; col < numCols; col++){
                if(mines.contains(board[row][col])){
                    board[row][col].setText("💣");
                }
            }
        }
    }
    private void createNumbers(){
        //OPTIMIZE LATER - (sliding window)
        for(int row = 0; row < numRows; row++){
            for(int col = 0; col < numCols; col++){
                if(mines.contains(board[row][col])){
                    continue;
                }
                int topRow = row - 1;
                int bottomRow = row + 1;
                int numBombs = 0;
                for(int c = col - 1; c < col + 2; c++){
                    if(c >= 0 && c < numCols){
                        if(topRow >= 0){
                            numBombs += checkForMines(topRow, c);
                        }
                        if(bottomRow < numRows){
                            numBombs += checkForMines(bottomRow, c);
                        }
                    }
                }
                if((col - 1) >= 0){
                    numBombs += checkForMines(row, col - 1);
                }
                if((col + 1) < numCols){
                    numBombs += checkForMines(row, col + 1);
                }
                board[row][col].num = numBombs;
            }
        }
    }
    private void openZeros(int row, int col){
        if(row >= numRows || row < 0 || col < 0 || col >= numCols){
            return;
        }
        if(board[row][col].revealed == false && board[row][col].num == 0 && !board[row][col].isMine){
                board[row][col].revealed = true;
                board[row][col].setText("0");
            
               openZeros(row-1, col);
               openZeros(row+1, col);
               openZeros(row, col-1);
               openZeros(row, col+1);
        }
        else if(!board[row][col].isMine){
            board[row][col].revealed = true;
            board[row][col].setText(String.valueOf(board[row][col].num));
            return;
        }
    }
    private int checkForMines(int row, int col){
        if(mines.contains(board[row][col])){
            return 1;
        }
        return 0;
    }
    private void plantFlag(Tile tile){
        tile.setText("🏁");
        for(int i = 0; i < mines.size(); i++){
            Tile localMine = mines.get(i);
            if(tile.equals(localMine)){
                mines.remove(i);
                numMines--;
                System.out.println(numMines);
                break;
            }
        }
    }
    
}
