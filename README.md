# Sparse Matrix Operations

This project implements efficient operations (addition, subtraction, multiplication) for large sparse matrices using a custom hash table for storage. The matrices are read from and written to files in a simple text format.

## Features

- **Efficient Storage:** Only non-zero elements are stored using a hash table.
- **Supported Operations:** Addition, subtraction, and multiplication of sparse matrices.
- **File I/O:** Matrices are loaded from and saved to files with a specific format.
- **Command-Line Interface:** Interactive prompts for operation and file selection.

## File Structure

```
code/
  src/
    SparseMatrix.java
    SparseMatrix.class
    SparseMatrix$MatrixElement.class
    output/
      addition_result.txt
      subtraction_result.txt
      multiplication_result.txt
sample_inputs/
  test1.txt
  test2.txt
  ...
```

## Matrix File Format

Each matrix file should start with the number of rows and columns, followed by non-zero entries:

```
rows=3
cols=3
(0, 0, 5)
(1, 2, 8)
(2, 1, -3)
```

## How to Run

1. **Compile the code:**

   ```sh
   javac code/src/SparseMatrix.java
   ```

2. **Run the program:**

   ```sh
   java -cp code/src SparseMatrix
   ```

3. **Follow the prompts:**
   - Choose the operation (add, subtract, multiply).
   - Enter the paths to the two matrix files (e.g., `sample_inputs/test1.txt`).

4. **Results:**
   - The result will be saved in the `code/src/output/` directory as `addition_result.txt`, `subtraction_result.txt`, or `multiplication_result.txt`.

## Example

```
Sparse Matrix Operations
1. Add two matrices
2. Subtract two matrices
3. Multiply two matrices
Enter your choice (1-3): 1
Enter the path for the first matrix file: sample_inputs/test1.txt
Enter the path for the second matrix file: sample_inputs/test2.txt
...
Result saved to: output/addition_result.txt
```

## Notes

- Only non-zero elements are stored and output.
- Input files must follow the specified format.
- Output files are overwritten each run.

## License

MIT License