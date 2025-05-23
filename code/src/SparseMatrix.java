import java.io.*;

class MatrixNode {
    int row;
    int col;
    int value;
    MatrixNode next;
    
    public MatrixNode(int row, int col, int value) {
        this.row = row;
        this.col = col;
        this.value = value;
        this.next = null;
    }
}

class CustomHashTable {
    private static final int INITIAL_SIZE = 16384;
    private MatrixNode[] buckets;
    private int size;
    private int capacity;
    
    public CustomHashTable() {
        this.capacity = INITIAL_SIZE;
        this.buckets = new MatrixNode[capacity];
        this.size = 0;
    }
    
    private int hash(int row, int col) {
        long combined = ((long)row << 16) | col;
        return (int)(combined % capacity);
    }
    
    public void put(int row, int col, int value) {
        if (value == 0) {
            remove(row, col);
            return;
        }
        
        int index = hash(row, col);
        MatrixNode current = buckets[index];
        
        while (current != null) {
            if (current.row == row && current.col == col) {
                current.value = value;
                return;
            }
            current = current.next;
        }
        
        MatrixNode newNode = new MatrixNode(row, col, value);
        newNode.next = buckets[index];
        buckets[index] = newNode;
        size++;
        
        if (size > capacity * 0.75) {
            resize();
        }
    }
    
    public int get(int row, int col) {
        int index = hash(row, col);
        MatrixNode current = buckets[index];
        
        while (current != null) {
            if (current.row == row && current.col == col) {
                return current.value;
            }
            current = current.next;
        }
        
        return 0;
    }
    
    public void remove(int row, int col) {
        int index = hash(row, col);
        MatrixNode current = buckets[index];
        MatrixNode prev = null;
        
        while (current != null) {
            if (current.row == row && current.col == col) {
                if (prev == null) {
                    buckets[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return;
            }
            prev = current;
            current = current.next;
        }
    }
    
    private void resize() {
        MatrixNode[] oldBuckets = buckets;
        int oldCapacity = capacity;
        
        capacity *= 2;
        buckets = new MatrixNode[capacity];
        size = 0;
        
        for (int i = 0; i < oldCapacity; i++) {
            MatrixNode current = oldBuckets[i];
            while (current != null) {
                put(current.row, current.col, current.value);
                current = current.next;
            }
        }
    }
    
    public MatrixNode[] getAllEntries() {
        MatrixNode[] entries = new MatrixNode[size];
        int count = 0;
        
        for (int i = 0; i < capacity; i++) {
            MatrixNode current = buckets[i];
            while (current != null) {
                entries[count++] = current;
                current = current.next;
            }
        }
        
        return entries;
    }
    
    public int getSize() {
        return size;
    }
}

public class SparseMatrix {
    private int numRows;
    private int numCols;
    private CustomHashTable matrix;
    
    public SparseMatrix(String matrixFilePath) {
        this.matrix = new CustomHashTable();
        loadFromFile(matrixFilePath);
    }
    
    public SparseMatrix(int numRows, int numCols) {
        if (numRows <= 0 || numCols <= 0) {
            throw new IllegalArgumentException("Matrix dimensions must be positive");
        }
        this.numRows = numRows;
        this.numCols = numCols;
        this.matrix = new CustomHashTable();
    }
    
    private void loadFromFile(String filePath) {
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader reader = new BufferedReader(fileReader);
            
            try {
                parseDimensions(reader);
                parseMatrixEntries(reader);
            } finally {
                reader.close();
                fileReader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + e.getMessage());
        }
    }
    
    private void parseDimensions(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null || !line.startsWith("rows=")) {
            throw new IllegalArgumentException("Input file has wrong format");
        }
        
        try {
            this.numRows = Integer.parseInt(line.substring(5).trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Input file has wrong format");
        }
        
        line = reader.readLine();
        if (line == null || !line.startsWith("cols=")) {
            throw new IllegalArgumentException("Input file has wrong format");
        }
        
        try {
            this.numCols = Integer.parseInt(line.substring(5).trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Input file has wrong format");
        }
        
        if (this.numRows <= 0 || this.numCols <= 0) {
            throw new IllegalArgumentException("Input file has wrong format");
        }
    }
    
    private void parseMatrixEntries(BufferedReader reader) throws IOException {
        String line;
        int lineNumber = 2;
        int skippedCount = 0;
        int processedCount = 0;
        
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            line = line.trim();
            
            if (line.length() == 0) {
                continue;
            }
            
            if (parseMatrixEntry(line, lineNumber)) {
                processedCount++;
            } else {
                skippedCount++;
            }
            
            if (lineNumber % 10000 == 0) {
                System.out.print(".");
            }
        }
        
        if (lineNumber > 10000) {
            System.out.println();
        }
        
        if (skippedCount > 0) {
            System.out.println("Processed " + processedCount);
        }
    }
    
    private boolean parseMatrixEntry(String line, int lineNumber) {
        if (!line.startsWith("(") || !line.endsWith(")")) {
            throw new IllegalArgumentException("Input file has wrong format at line " + lineNumber);
        }
        
        String content = line.substring(1, line.length() - 1);
        String[] parts = customSplit(content, ',');
        if (parts.length != 3) {
            throw new IllegalArgumentException("Input file has wrong format at line " + lineNumber);
        }
        
        try {
            int row = customParseInt(parts[0].trim());
            int col = customParseInt(parts[1].trim());
            int value = customParseInt(parts[2].trim());
            
            if (row < 0 || row >= numRows || col < 0 || col >= numCols) {
                return false;
            }
            
            if (value != 0) {
                setElement(row, col, value);
            }
            return true;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Input file has wrong format at line " + lineNumber);
        }
    }
    
    private String[] customSplit(String str, char delimiter) {
        int count = 1;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == delimiter) {
                count++;
            }
        }
        
        String[] result = new String[count];
        int start = 0;
        int index = 0;
        
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == delimiter) {
                result[index++] = str.substring(start, i);
                start = i + 1;
            }
        }
        result[index] = str.substring(start);
        
        return result;
    }
    
    private int customParseInt(String str) throws NumberFormatException {
        str = str.trim();
        
        if (str.indexOf('.') != -1) {
            throw new NumberFormatException("Floating point values not allowed");
        }
        
        if (str.length() == 0) {
            throw new NumberFormatException("Empty string");
        }
        
        int result = 0;
        boolean negative = false;
        int i = 0;
        
        if (str.charAt(0) == '-') {
            negative = true;
            i = 1;
        } else if (str.charAt(0) == '+') {
            i = 1;
        }
        
        for (; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                throw new NumberFormatException("Invalid integer format");
            }
            result = result * 10 + (c - '0');
        }
        
        return negative ? -result : result;
    }
    
    public int getElement(int currRow, int currCol) {
        if (currRow < 0 || currRow >= numRows || currCol < 0 || currCol >= numCols) {
            throw new IndexOutOfBoundsException("Matrix index out of bounds");
        }
        return matrix.get(currRow, currCol);
    }
    
    public void setElement(int currRow, int currCol, int value) {
        if (currRow < 0 || currRow >= numRows || currCol < 0 || currCol >= numCols) {
            throw new IndexOutOfBoundsException("Matrix index out of bounds");
        }
        matrix.put(currRow, currCol, value);
    }
    
    //Addition of Matrices
    public SparseMatrix add(SparseMatrix other) {
        if (this.numRows != other.numRows || this.numCols != other.numCols) {
            throw new IllegalArgumentException("Matrix dimensions must match for addition");
        }
        
        SparseMatrix result = new SparseMatrix(numRows, numCols);
        
        MatrixNode[] entries1 = this.matrix.getAllEntries();
        for (MatrixNode entry : entries1) {
            if (entry != null) {
                result.setElement(entry.row, entry.col, entry.value);
            }
        }
        
        MatrixNode[] entries2 = other.matrix.getAllEntries();
        for (MatrixNode entry : entries2) {
            if (entry != null) {
                int currentVal = result.getElement(entry.row, entry.col);
                result.setElement(entry.row, entry.col, currentVal + entry.value);
            }
        }
        
        return result;
    }
    
    //Subtraction of Matrices
    public SparseMatrix subtract(SparseMatrix other) {
        if (this.numRows != other.numRows || this.numCols != other.numCols) {
            throw new IllegalArgumentException("Matrix dimensions must match for subtraction");
        }
        
        SparseMatrix result = new SparseMatrix(numRows, numCols);
        
        MatrixNode[] entries1 = this.matrix.getAllEntries();
        for (MatrixNode entry : entries1) {
            if (entry != null) {
                result.setElement(entry.row, entry.col, entry.value);
            }
        }
        
        MatrixNode[] entries2 = other.matrix.getAllEntries();
        for (MatrixNode entry : entries2) {
            if (entry != null) {
                int currentVal = result.getElement(entry.row, entry.col);
                result.setElement(entry.row, entry.col, currentVal - entry.value);
            }
        }
        
        return result;
    }

    //Multiply Matrices
    public SparseMatrix multiply(SparseMatrix other) {
        if (this.numCols != other.numRows) {
            throw new IllegalArgumentException("Matrix dimensions incompatible for multiplication");
        }
        
        SparseMatrix result = new SparseMatrix(this.numRows, other.numCols);
        MatrixNode[] entries1 = this.matrix.getAllEntries();
        MatrixNode[] entries2 = other.matrix.getAllEntries();
        
        for (MatrixNode entry1 : entries1) {
            if (entry1 == null) continue;
            
            int i = entry1.row;
            int k = entry1.col;
            int aik = entry1.value;
            
            for (MatrixNode entry2 : entries2) {
                if (entry2 == null) continue;
                
                if (entry2.row == k) {
                    int j = entry2.col;
                    int bkj = entry2.value;
                    int currentVal = result.getElement(i, j);
                    result.setElement(i, j, currentVal + aik * bkj);
                }
            }
        }
        
        return result;
    }
    
    // Save output to file
    public void saveToFile(String filePath) {
        try {
            File file = new File(filePath);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter writer = new BufferedWriter(fileWriter, 65536); // 64KB buffer
            
            try {
                writer.write("rows=" + numRows);
                writer.newLine();
                writer.write("cols=" + numCols);
                writer.newLine();
                
                MatrixNode[] entries = matrix.getAllEntries();
                mergeSort(entries, 0, entries.length - 1);
                
                for (MatrixNode entry : entries) {
                    if (entry != null) {
                        writer.write("(" + entry.row + ", " + entry.col + ", " + entry.value + ")");
                        writer.newLine();
                    }
                }
            } finally {
                writer.close();
                fileWriter.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not write to file: " + filePath);
        }
    }
    
    // In-place merge sort implementation
    private void mergeSort(MatrixNode[] arr, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }
    
    private void merge(MatrixNode[] arr, int left, int mid, int right) {
        MatrixNode[] temp = new MatrixNode[right - left + 1];
        int i = left, j = mid + 1, k = 0;
        
        while (i <= mid && j <= right) {
            if (arr[i] == null) {
                i++;
                continue;
            }
            if (arr[j] == null) {
                j++;
                continue;
            }
            
            if (arr[i].row < arr[j].row || 
                (arr[i].row == arr[j].row && arr[i].col < arr[j].col)) {
                temp[k++] = arr[i++];
            } else {
                temp[k++] = arr[j++];
            }
        }
        
        while (i <= mid) {
            temp[k++] = arr[i++];
        }
        
        while (j <= right) {
            temp[k++] = arr[j++];
        }
        
        System.arraycopy(temp, 0, arr, left, temp.length);
    }
    
    public int getNonZeroCount() {
        return matrix.getSize();
    }
    
    public int getNumRows() {
        return numRows;
    }
    
    public int getNumCols() {
        return numCols;
    }
    
    public static void main(String[] args) {
        try {
            System.out.println("Sparse Matrix Operations");
            System.out.println("1. Add two matrices");
            System.out.println("2. Subtract two matrices");  
            System.out.println("3. Multiply two matrices");
            System.out.print("Enter your choice (1-3): ");
            
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
            String choiceStr = inputReader.readLine().trim();
            int choice = Integer.parseInt(choiceStr);
            
            if (choice < 1 || choice > 3) {
                System.out.println("Invalid choice. Please enter a number between 1 and 3.");
                return;
            }
            
            System.out.print("Enter the path for the first matrix file: ");
            String file1 = inputReader.readLine().trim();
            
            System.out.println("Loading first matrix...");
            long startTime = System.currentTimeMillis();
            SparseMatrix matrix1 = new SparseMatrix(file1);
            long loadTime = System.currentTimeMillis() - startTime;
            System.out.println("First matrix loaded in " + loadTime + "ms (" + 
                             matrix1.getNumRows() + "x" + matrix1.getNumCols() + 
                             ", " + matrix1.getNonZeroCount() + " non-zero elements)");
            
            System.out.print("Enter the path for the second matrix file: ");
            String file2 = inputReader.readLine().trim();
            
            System.out.println("Loading second matrix...");
            startTime = System.currentTimeMillis();
            SparseMatrix matrix2 = new SparseMatrix(file2);
            loadTime = System.currentTimeMillis() - startTime;
            System.out.println("Second matrix loaded in " + loadTime + "ms (" + 
                             matrix2.getNumRows() + "x" + matrix2.getNumCols() + 
                             ", " + matrix2.getNonZeroCount() + " non-zero elements)");
            
            String outputDir = "output";
            SparseMatrix result;
            String outputFile;
            String operationName;
            
            System.out.println("Performing operation...");
            startTime = System.currentTimeMillis();
            
            switch (choice) {
                case 1:
                    result = matrix1.add(matrix2);
                    outputFile = outputDir + "/addition_result.txt";
                    operationName = "Addition";
                    break;
                case 2:
                    result = matrix1.subtract(matrix2);
                    outputFile = outputDir + "/subtraction_result.txt";
                    operationName = "Subtraction";
                    break;
                case 3:
                    result = matrix1.multiply(matrix2);
                    outputFile = outputDir + "/multiplication_result.txt";
                    operationName = "Multiplication";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid choice");
            }
            
            long operationTime = System.currentTimeMillis() - startTime;
            System.out.println(operationName + " completed in " + operationTime + "ms");
            
            System.out.println("Saving result...");
            startTime = System.currentTimeMillis();
            result.saveToFile(outputFile);
            long saveTime = System.currentTimeMillis() - startTime;
            
            System.out.println("Result saved in " + saveTime + "ms");
            System.out.println("Final result: " + result.getNumRows() + "x" + result.getNumCols() + 
                             " matrix with " + result.getNonZeroCount() + " non-zero elements");
            System.out.println("Result saved to: " + outputFile);
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}