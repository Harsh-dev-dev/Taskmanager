import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanagerapp.R
import com.example.taskmanagerapp.Task
import com.example.taskmanagerapp.viewmodel.TaskViewModel

class TaskAdapter(private val listener: TaskListener, private val recyclerView: RecyclerView,
                  private val taskViewModel: TaskViewModel
) : ListAdapter<Task,
        TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    private val itemTouchHelper: ItemTouchHelper
    private var highlightedPosition: Int? = null
    private var removedTaskCount = 0
    private var presentTaskCount = 0

    init {
        presentTaskCount = currentList.size
        itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = currentList[position]

                // Check if task is selected
                if (!task.isSelected) {
                    listener.showSelectTaskMessage()
                    notifyItemChanged(position)
                    return
                }

                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        // Delete action
                        listener.onTaskSwiped(task, TaskAction.DELETE)
                    }
                    ItemTouchHelper.RIGHT -> {
                        // Edit action
                        listener.onTaskSwiped(task, TaskAction.EDIT)
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val editIcon = itemView.findViewById<ImageView>(R.id.editIcon)
                val deleteIcon = itemView.findViewById<ImageView>(R.id.deleteIcon)


                if (dX < 0) {
                    editIcon.visibility = View.INVISIBLE
                    deleteIcon.visibility = View.VISIBLE
                } else if (dX > 0) { // Swiping right (edit)
                    editIcon.visibility = View.VISIBLE
                    deleteIcon.visibility = View.INVISIBLE
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task, position == highlightedPosition) { completedTask ->
            removeTask(completedTask)
        }
    }

    private fun removeTask(task: Task) {
        val position = currentList.indexOf(task)
        if (position != -1) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(position) as TaskViewHolder?

            viewHolder?.itemView?.animate()
                ?.alpha(0f)
                ?.setDuration(1000)
                ?.withEndAction {
                    val updatedList = currentList.toMutableList()
                    updatedList.removeAt(position)
                    submitList(updatedList)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, updatedList.size)

                    removedTaskCount++
                    presentTaskCount = updatedList.size

                }
        }

    }




    fun highlightItem(position: Int) {
        val previousHighlightedPosition = highlightedPosition
        highlightedPosition = position
        previousHighlightedPosition?.let { notifyItemChanged(it) }
        notifyItemChanged(position)
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        private val taskDescription: TextView = itemView.findViewById(R.id.taskDescription)
        private val taskDueDate: TextView = itemView.findViewById(R.id.taskDueDate)
        private val priorityIndicator: View = itemView.findViewById(R.id.priorityIndicator)
        private val editIcon: ImageView = itemView.findViewById(R.id.editIcon)
        private val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIcon)
        private val taskContainer: RelativeLayout = itemView.findViewById(R.id.taskContainer)
        private val checkBox: CheckBox = itemView.findViewById(R.id.taskCheckbox)


        init {
            taskContainer.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val task = getItem(position)
                    listener.onTaskClicked(task)
                }
            }
        }

        fun bind(task: Task, isHighlighted: Boolean, onTaskCompleted: (Task) -> Unit) {
            taskTitle.text = task.title
            taskDescription.text = task.description
            taskDueDate.text = task.dueDate
            checkBox.isChecked = task.isCompleted

            // Set priority indicator color
            when (task.priority) {
                "High" -> priorityIndicator.setBackgroundResource(R.drawable.priority_indicator_red)
                "Medium" -> priorityIndicator.setBackgroundResource(R.drawable.priority_indicator_yellow)
                "Low" -> priorityIndicator.setBackgroundResource(R.drawable.priority_indicator_blue)
            }

            // Handle click events
            editIcon.setOnClickListener { listener.onTaskActionClicked(task, TaskAction.EDIT) }
            deleteIcon.setOnClickListener { listener.onTaskActionClicked(task, TaskAction.DELETE) }

            if (isHighlighted) {
                // Start blinking animation
                val blinkAnimator = ValueAnimator.ofArgb(
                    Color.TRANSPARENT,
                    Color.parseColor("#C6EEFA")
                )
                blinkAnimator.duration = 400 // Blinking duration for each blink
                blinkAnimator.repeatMode = ValueAnimator.REVERSE
                blinkAnimator.repeatCount = 5 // Number of times to blink
                blinkAnimator.addUpdateListener { animator ->
                    itemView.setBackgroundColor(animator.animatedValue as Int)
                }
                blinkAnimator.start()

                Handler(Looper.getMainLooper()).postDelayed({
                    val background = GradientDrawable().apply {
                        setColor(Color.argb(11, 22, 33, 12))
                        cornerRadius = 16f
                    }
                    itemView.background = background
                }, 5000)
            } else {
                val background = GradientDrawable().apply {
                    setColor(Color.argb(11, 22, 33, 12))
                    cornerRadius = 16f
                }
                itemView.background = background
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    onTaskCompleted(task)
                    taskViewModel.deleteTask(task)
                }
            }
        }
    }




    interface TaskListener {
        fun onTaskActionClicked(task: Task, action: TaskAction)
        fun onTaskSwiped(task: Task, action: TaskAction)
        fun showSelectTaskMessage()
        fun onTaskClicked(task: Task)
    }

    enum class TaskAction {
        EDIT,
        DELETE
    }

    fun enableSwipeToDelete(recyclerView: RecyclerView) {
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}



class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }
}
