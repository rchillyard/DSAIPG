from .solution import Solution

class CodeLength:
    """
    The CodeLength class serves as the entry point for the application.
    It uses the Solution class to perform computations and displays the results for a range of input values.
    """
    @staticmethod
    def main():
        # TODO code application logic here
        s1 = Solution()
        # n = 3
        # length = 1
        # print("The ans is " + str(s1.work1(n, length)))
        for n in range(10):
            for i in range(10):
                print(f"The ans for n={n}, i={i} is {s1.work1(n, i)}")

if __name__ == "__main__":
    CodeLength.main()
