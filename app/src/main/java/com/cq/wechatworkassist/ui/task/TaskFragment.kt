package com.cq.wechatworkassist.ui.task

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cq.wechatworkassist.R
import com.cq.wechatworkassist.task.Task
import com.cq.wechatworkassist.task.TaskManager
import com.cq.wechatworkassist.util.MD5Tools
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.adapter_item_task.view.*
import kotlinx.android.synthetic.main.fragment_tasks.view.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class TaskFragment : Fragment() {
    private var galleryViewModel: GalleryViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
            ViewModelProviders.of(this).get(GalleryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_tasks, container, false)
        refreshData()
        setHasOptionsMenu(true)
        return root
    }

    private val mAdapter = MyAdapter()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.recyclerView.adapter = mAdapter
        view.recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }

    private fun openSystemFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        // 所有类型
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择文件"), 1)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Toast.makeText(activity, "请安装文件管理器", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            //Get the Uri of the selected file
            val uri: Uri? = data?.data
            if (null != uri) {
                val path: String? = FileUtil.getPath(requireActivity(), uri)
                readFile(uri, path)
            }
        }
    }

    fun readFile(uri: Uri, path: String?){
        val parcelFileDescriptor = context?.contentResolver?.openFileDescriptor(uri, "r", null)

        parcelFileDescriptor?.let {
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val file = File(context?.cacheDir, MD5Tools.toMD5(path))
            val outputStream = FileOutputStream(file)
//            IOUtils.copy(inputStream, outputStream)
//            val baos = ByteArrayOutputStream()
            inputStream.use { it.copyTo(outputStream) }

            Log.i("cuiqing filepath", " = $path")
            val result: ArrayList<Task> = ArrayList()
            file.inputStream().bufferedReader(charset("GBK")).useLines { lines ->
                lines.forEach {
                    if (TextUtils.isEmpty(it)) return@forEach
                    var array = it.trim().split(",")
                    if (TextUtils.isEmpty(array[1])) return@forEach
                    var task =
                        Task(array[0], array[1])
                    result.add(task)
                }
            }
            TaskManager.importData(taskList = result)
            refreshData()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_task, menu)
    }


    @SuppressLint("CheckResult")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_select) {
            val rxPermissions = RxPermissions(requireActivity())
            rxPermissions.requestEach( Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe {
                    openSystemFile()
                }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private var mData: List<Task>? = null

    fun refreshData(){
        mData = TaskManager.getAllTask()
        mAdapter.notifyDataSetChanged()
    }
    private inner class MyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_item_task, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as MyViewHolder).bindData(mData?.get(position))
        }

        override fun getItemCount(): Int {
            return mData?.size ?: 0
        }

        private inner class MyViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bindData(task: Task?) {
                itemView.phone.text = task?.phone
                itemView.status.text = task?.status
                itemView.desc.text = task?.content
                if (task?.name != null) {
                    itemView.name.text = task.name
                }
            }

        }
    }
}