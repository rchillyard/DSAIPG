import math
import sys


class RunLength:
    """
    This class supports the concept of run-length encoding.
    
    The RunLength class provides functionality to calculate the probability
    of runs of a specific length and color (black or white) in a sequence
    based on the given probability of a black pixel.
    """
    def __init__(self, p_pixel_black: float):
        """
        Constructs a RunLength object with the specified probability of a black pixel.
        
        :param p_pixel_black: the probability of a black pixel, a value between 0.0 and 1.0
        """
        self.p_pixel_black = p_pixel_black

    def probability_of_run_length(self, n: int, black: bool) -> float:
        """
        Calculates the probability of a run of a specific length and color (black or white).
        A run is defined as a consecutive sequence of pixels of the same color.
        
        :param n: The length of the run.
        :param black: A boolean indicating the color of the run.
                      If true, calculates the probability for black runs; otherwise, for white runs.
        :return: The probability of a run of the given length and color.
        """
        if black:
            return math.pow(self.p_pixel_black, n) * (1 - self.p_pixel_black)
        else:
            return math.pow(1 - self.p_pixel_black, n) * self.p_pixel_black

    @staticmethod
    def main(args: list):
        """
        The entry point for the program. Calculates and displays the probabilities,
        totals, and expectations of run lengths for black and white pixels based
        on the given probability of a black pixel and a maximum run length.
        
        :param args: Command-line arguments. The first argument (optional) represents
                     the maximum run length (default is 25). The second argument
                     (optional) represents the probability of a black pixel (default is 0.25).
        """
        n = int(args[0]) if len(args) > 0 else 25
        p_black = float(args[1]) if len(args) > 1 else 0.25
        
        print(f"RunLength with n = {n} and p(black) = {p_black}")
        print("In the following, i is the length of a run of the given color; p(i) is the probability of a run with that length")
        
        run_length = RunLength(p_black)
        total_black = 0.0
        total_white = 0.0
        expectation_black = 0.0
        expectation_white = 0.0
        
        for i in range(1, n):
            p_i_black = run_length.probability_of_run_length(i, True)
            p_i_white = run_length.probability_of_run_length(i, False)
            total_black += p_i_black
            total_white += p_i_white
            expectation_black += p_i_black * i
            expectation_white += p_i_white * i
            print(f"i = {i} (black), p(i) = {p_i_black}")
            print(f"i = {i} (white), p(i) = {p_i_white}")
            
        print(f"total = {total_black} for black")
        print(f"total = {total_white} for white")
        print(f"expectation = {expectation_black} for black")
        print(f"expectation = {expectation_white} for white")

if __name__ == "__main__":
    RunLength.main(sys.argv[1:])
