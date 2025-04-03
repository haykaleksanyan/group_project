public class Ship {
    private int size; // size of the ship
    private int startRow;
    private int startCol;
    private boolean isHorizontal;
    private boolean[] hits;

    public Ship(int size, int startRow, int startCol, boolean isHorizontal){
        this.size = size;
        this.startRow = startRow;
        this.startCol = startCol;
        this.isHorizontal = isHorizontal;
        this.hits = new boolean[size];
    }

    public int getSize() {
        return size;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public boolean getisHorizontal() {
        return isHorizontal;
    }

    public boolean[] getHits() {
        return hits;
    }

    public Ship(Ship that){
        this.size = that.size;
        this.startRow = that.startRow;
        this.startCol = that.startCol;
        this.isHorizontal = that.isHorizontal;

        this.hits = new boolean[size];
        for(int i = 0; i < size; i++){
            this.hits[i] = that.hits[i];
        }
    }

    public boolean isSunk(){
        for (int i = 0; i < size; i++){
            if(!hits[i]){
                return false;
            }
        }
        return true;
    }

    public boolean isOcuupied(int row, int col) {
        if (isHorizontal) {
            return (row == startRow) && (col >= startCol) && (col < startCol + size);
        } else {
            return (col == startCol) && (row >= startRow) && (row < startRow + size);
        }
    }

    public void registerHit(int row, int col) {
        if (isOcuupied(row, col)) {
            if(isHorizontal){
                hits[col - startCol] = true;
            } else{
                hits[row - startRow] = true;
            }
        }
    }



}
