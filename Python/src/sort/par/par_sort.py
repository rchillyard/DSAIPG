"""
Parallel merge sort, ported from sort/par/ParSort.java.

The array is split in two, each half is sorted somewhere else, and the two
sorted halves are merged. Below a cutoff the split stops being worth its cost
and the range is sorted sequentially instead. Finding that cutoff is the point
of the exercise: too low and the coordination costs more than the sorting, too
high and the second core sits idle.

Two things differ from the Java, and both are forced by how Python runs.

**Processes, not threads.** CPython holds a lock that lets only one thread
execute bytecode at a time, so threads give no speedup for work like this.
Java's CompletableFuture uses the common ForkJoinPool, which is genuinely
parallel; the equivalent here is a ProcessPoolExecutor.

**array.array('i'), not list.** Data crossing a process boundary must be
serialized, and this is where the choice pays for itself. A round trip of a
million ints, measured::

    list    dumps 10.4 ms   loads 19.0 ms      29.4 ms
    array   dumps  0.5 ms   loads  0.3 ms       0.7 ms

Forty times less, because a list is a million pointers and every referenced
object must be visited, while an ``array.array('i')`` is a contiguous block that
pickle protocol 5 ships as a buffer. Note that the load side dominates -- it is
75 times cheaper -- which matters because every chunk is loaded twice, once by
the worker and once by the parent taking the result back.

It is NOT about size: a list of small ints actually pickles smaller than the
array (2.75 MB against 4.00 MB for values below 1000), since small ints need a
byte or two each while the array always spends four. The saving is entirely in
the work of building and walking a million separate objects.

It is also the faithful analogue of Java's ``int[]``.

Even so, expect the useful cutoff to be far higher here than in Java. Java's
threads share the heap and copy nothing; every chunk sent to a worker here is
serialized, sent, deserialized, and the result sent back the same way.
"""

from __future__ import annotations

from array import array
from concurrent.futures import Executor, Future

#: The size below which a range is sorted sequentially rather than split.
#: 1000 is the Java's value; the right value here is larger, and finding it is
#: what Main.py measures.
cutoff = 1000

#: The type code for a signed 32-bit int, matching Java's int[].
TYPE_CODE = "i"


def sort(xs: array, from_: int, to: int, executor: Executor | None = None) -> None:
    """
    Sort xs[from_:to] in place, in parallel where the range is large enough.

    :param xs: an ``array.array('i')`` to sort.
    :param from_: the index of the first element to sort.
    :param to: one past the index of the last.
    :param executor: where to run the halves. None sorts sequentially, which is
                     what a worker process does -- submitting to the pool it is
                     already running in would deadlock.
    """
    if to - from_ >= cutoff and executor is not None:
        # TO BE IMPLEMENTED : sort the two halves in parallel, merge the results,
        # and write them back into xs[from_:to]. Use async_sort and do_merge.
        raise NotImplementedError("TO BE IMPLEMENTED")
    else:
        xs[from_:to] = array(TYPE_CODE, sorted(xs[from_:to]))


def sort_recursive(chunk: array) -> array:
    """
    Sort a chunk and return the result, leaving the chunk alone.

    This is what runs in a worker process, so it takes and returns a whole
    ``array.array`` rather than an array with indices: only the chunk crosses the
    process boundary, not the array it came from.

    NOTE the Java's version of this calls sort again, so the recursion may split
    further in the worker. Here it sorts sequentially. Submitting more work to
    the pool from inside a worker would deadlock, since the worker occupies one
    of the pool's slots while it waits.

    :param chunk: the values to sort.
    :return: a new array holding them in order.
    """
    # TO BE IMPLEMENTED : return a sorted copy of chunk, leaving chunk unchanged
    raise NotImplementedError("TO BE IMPLEMENTED")


def do_merge(xs1: array, xs2: array) -> array:
    """
    Merge two sorted arrays into one.

    :param xs1: the first sorted array.
    :param xs2: the second.
    :return: a new array holding every element of both, in order.
    """
    result = array(TYPE_CODE, [0] * (len(xs1) + len(xs2)))
    i = 0
    j = 0
    for k in range(len(result)):
        if i >= len(xs1):
            result[k] = xs2[j]
            j += 1
        elif j >= len(xs2):
            result[k] = xs1[i]
            i += 1
        elif xs2[j] < xs1[i]:
            result[k] = xs2[j]
            j += 1
        else:
            result[k] = xs1[i]
            i += 1
    return result


def async_sort(xs: array, from_: int, to: int, executor: Executor) -> Future:
    """
    Start sorting xs[from_:to] somewhere else.

    :param xs: the array holding the range.
    :param from_: the index of the first element.
    :param to: one past the index of the last.
    :param executor: where to run it.
    :return: a Future giving the sorted chunk.
    """
    return executor.submit(sort_recursive, xs[from_:to])
