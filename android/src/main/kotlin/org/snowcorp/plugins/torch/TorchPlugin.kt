package org.snowcorp.plugins.torch

import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import androidx.lifecycle.LifecycleEventObserver
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.lifecycle.HiddenLifecycleReference
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import org.snowcorp.plugins.torch.impl.BaseTorch
import org.snowcorp.plugins.torch.impl.TorchCamera1Impl
import org.snowcorp.plugins.torch.impl.TorchCamera2Impl
import org.snowcorp.plugins.torch.utils.ActivityLifecycleCallbacks

/** TorchPlugin */
@Suppress("JoinDeclarationAndAssignment")
class TorchPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var activity: Activity
  private lateinit var torchImpl: BaseTorch

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "snowcorp/torch")
    channel.setMethodCallHandler(this)
  }

  fun initTorch() {
    torchImpl = when {
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> TorchCamera2Impl(activity)
      else -> TorchCamera1Impl(activity)
    }

    activity.application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks() {
      override fun onActivityStopped(activity: Activity) {
        torchImpl.dispose()
      }
    })
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    val hasLamp = activity.applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    if (call.method == "turnOn") {
      if (!hasLamp) {
        result.error("NOTORCH", "This device does not have a flash", null)
      } else {
        torchImpl.turnOn()
        result.success(true)
      }
    } else if (call.method == "turnOff") {
      if (!hasLamp) {
        result.error("NOTORCH", "This device does not have a flash", null)
      } else {
        torchImpl.turnOff()
        result.success(true)
      }
    } else if (call.method == "hasTorch") {
      result.success(hasLamp)
    } else if (call.method == "dispose") {
      torchImpl.dispose()
      result.success(true)
    } else if (call.method == "getPlatformVersion") {
      result.success("Android ${Build.VERSION.RELEASE}")
    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  // ActivityAware
  override fun onDetachedFromActivity() {
  }

  override fun onDetachedFromActivityForConfigChanges() {
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
    initTorch()
  }
}
