import json
import os
import matplotlib.pyplot as plt

BASE_DIR = os.path.dirname(os.path.abspath(__file__))

def load_avg_populate(filename):
    with open(os.path.join(BASE_DIR, filename)) as f:
        data = json.load(f)
    return data['summary']['avgPopulateDurationMs']

# --- Plot 1: Concurrency Scaling ---
thread_counts = [1, 2, 4, 6, 8, 10, 12]
baseline_threads = [load_avg_populate('perf-results-threads-%d-baseline.json' % t) for t in thread_counts]
partitioned_threads = [load_avg_populate('perf-results-threads-%d-partitioned.json' % t) for t in thread_counts]

# --- Plot 2: Data Volume Scaling ---
book_counts = [50000, 100000, 150000, 200000, 250000, 300000]
book_labels = ['50k', '100k', '150k', '200k', '250k', '300k']
baseline_books = [load_avg_populate('perf-results-books-%s-baseline.json' % l) for l in book_labels]
partitioned_books = [load_avg_populate('perf-results-books-%s-partitioned.json' % l) for l in book_labels]

fig, ((ax1, ax2), (ax3, ax4)) = plt.subplots(2, 2, figsize=(16, 12))

# Plot 1
ax1.plot(thread_counts, baseline_threads, '-o', color='red', label='Non-partitioned')
ax1.plot(thread_counts, partitioned_threads, '-o', color='blue', label='Partitioned')
ax1.set_xlabel('Thread Count')
ax1.set_ylabel('Avg Populate Duration (ms)')
ax1.set_title('Concurrency Scaling (100k books)')
ax1.set_xticks(thread_counts)
ax1.legend()
ax1.grid(True, alpha=0.3)

# Plot 2
ax2.plot(book_counts, baseline_books, '-o', color='red', label='Non-partitioned')
ax2.plot(book_counts, partitioned_books, '-o', color='blue', label='Partitioned')
ax2.set_xlabel('Number of Books')
ax2.set_ylabel('Avg Populate Duration (ms)')
ax2.set_title('Data Volume Scaling (4 threads)')
ax2.set_xticks(book_counts)
ax2.get_xaxis().set_major_formatter(plt.FuncFormatter(lambda x, _: '%dk' % (x / 1000)))
ax2.legend()
ax2.grid(True, alpha=0.3)

# Plot 3: Overhead Ratio
ratio = [p / b for p, b in zip(partitioned_books, baseline_books)]
ax3.plot(book_counts, ratio, '-o', color='purple')
ax3.axhline(y=1.0, color='gray', linestyle='--', alpha=0.7, label='Parity (1.0)')
ax3.set_xlabel('Number of Books')
ax3.set_ylabel('Partitioned / Non-partitioned')
ax3.set_title('Partitioned Overhead Ratio (4 threads)')
ax3.set_xticks(book_counts)
ax3.get_xaxis().set_major_formatter(plt.FuncFormatter(lambda x, _: '%dk' % (x / 1000)))
ax3.legend()
ax3.grid(True, alpha=0.3)

# Plot 4: Thread-count Overhead Ratio
ratio_threads = [p / b for p, b in zip(partitioned_threads, baseline_threads)]
ax4.plot(thread_counts, ratio_threads, '-o', color='purple')
ax4.axhline(y=1.0, color='gray', linestyle='--', alpha=0.7, label='Parity (1.0)')
ax4.set_xlabel('Thread Count')
ax4.set_ylabel('Partitioned / Non-partitioned')
ax4.set_title('Partitioned Overhead Ratio (100k books)')
ax4.set_xticks(thread_counts)
ax4.legend()
ax4.grid(True, alpha=0.3)

plt.tight_layout()
plt.savefig(os.path.join(BASE_DIR, 'perf-plots.png'), dpi=150)
print('Saved to perf-plots.png')
plt.show()
