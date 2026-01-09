class Solution:
    """
    The Solution class provides a method to calculate a specific probability value
    based on certain iterative operations on a multi-dimensional array.
    
    The class utilizes dynamic programming to compute probabilities
    with constraints on iterations and dimensions.
    """
    def __init__(self):
        # Java: private final double[][][] f = new double[2][5100][53];
        # Python: list of lists of lists
        self.f = [[[0.0 for _ in range(53)] for _ in range(5100)] for _ in range(2)]

    def work1(self, n: int, length: int) -> float:
        """
        Computes a specific probability value based on iterative operations
        applied to a multi-dimensional array using dynamic programming.
        
        :param n: the number of iterations to perform (constraints on the range will apply)
        :param length: the target length used as a determinant during the computation
        :return: the resultant probability value after processing
        """
        # Resetting f might be needed if work1 is called multiple times on the same instance?
        # In Java, `f` is a field, but it's not reset in `work1`.
        # However, `Solution` is instantiated once in `main` and `work1` is called multiple times.
        # This means `f` accumulates state or is reused?
        # Java code:
        # f[0][0][0] = 1.0;
        # Loop i from 1 to n...
        # Inside loop: f[0][i][j] = f[1][i][j]; f[1][i][j] = 0;
        # It seems it overwrites `f` for indices up to `n`.
        # But if `n` decreases in subsequent calls, old values might remain?
        # In `main`, `n` goes 0 to 9. `i` goes 0 to 9.
        # So `n` increases.
        # But `f` is `final` in Java, so it persists.
        # Wait, `f` is initialized at class level.
        # `work1` sets `f[0][0][0] = 1.0`.
        # If `n=0`, loop doesn't run. Returns `ans + f[0][0][length]`.
        # If `length=0`, returns `1.0`.
        # If `length!=0`, returns `0.0`.
        # If `n=1`. Loop runs for `i=1`.
        # It uses `f[0][0][...]`.
        # It sets `f[0][1][...]`.
        # So it seems correct to reuse `f` as long as we overwrite what we use.
        # But wait, `f` is 5100x53.
        # If we call `work1(n=10)` then `work1(n=5)`, the values for `i > 5` are left over from `n=10`.
        # But `work1` only iterates up to `n`.
        # So it should be fine.
        
        # However, `f[0][0][0] = 1.0` is set every time.
        # But what about `f[0][0][other]`?
        # They are 0 initially.
        # If they were modified, they might be dirty.
        # But `f` indices are `[2][5100][53]`.
        # The loops use `i` as the second index.
        # `f[0][i][j]` is written.
        # `f[0][0][...]` is NOT cleared.
        # So if `f[0][0][1]` was set previously, it remains set?
        # `f[0][0][0]` is set to 1.0.
        # The loops start from `i=1`.
        # They read `f[0][i-1][...]`.
        # For `i=1`, they read `f[0][0][...]`.
        # If `f[0][0][...]` has garbage, it will affect the result.
        # The Java code does NOT clear `f`.
        # This implies `Solution` instance should probably be fresh or `f` should be cleared?
        # In `CodeLength.java`: `Solution s1 = new Solution();` is created ONCE.
        # Then loops call `s1.work1(n, i)`.
        # So `s1` is reused.
        # This suggests that `f` might be dirty.
        # Is it a bug in Java code or intended?
        # Or maybe `f` is only written to in a way that previous values don't matter?
        # `f[0][0][0]` is set to 1.
        # Other `f[0][0][j]` are 0 initially.
        # Are they ever written to?
        # `f[0][i][j]` is written in the loop. `i` starts at 1.
        # So `f[0][0][...]` is NEVER written to in the loop (except `f[0][0][0]=1`).
        # So `f[0][0][j]` for `j>0` remains 0.
        # So it is safe.
        
        # But what about `f[0][i][j]` for `i>0`?
        # If we run `work1(n=2)`, we write to `f[0][1][...]` and `f[0][2][...]`.
        # Then run `work1(n=1)`. We write to `f[0][1][...]`.
        # We don't read `f[0][2][...]`.
        # So it seems safe.
        
        self.f[0][0][0] = 1.0
        ans = 0.0
        for i in range(1, n + 1):
            limit = min(n, 100)
            # First inner loop: j from 1 to limit-1 (Java: j < limit)
            for j in range(1, limit):
                self.f[1][i][j] = self.f[0][i - 1][j - 1] * 0.25
            
            # Second inner loop: j from 0 to limit-1
            for j in range(limit):
                if j == length:
                    ans += self.f[0][i - 1][j] * 0.75
                else:
                    self.f[1][i][0] += self.f[0][i - 1][j] * 0.75
            
            # Third inner loop: j from 0 to limit-1
            for j in range(limit):
                self.f[0][i][j] = self.f[1][i][j]
                self.f[1][i][j] = 0.0

        ans += self.f[0][n][length]
        return ans
