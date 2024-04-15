package jp.otamay.cinemastudy.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import jp.otamay.cinemastudy.R
import jp.otamay.cinemastudy.base.BaseActivity
import jp.otamay.cinemastudy.fragment.M003ResearchFragment
import jp.otamay.cinemastudy.fragment.M004ManualInputFragment

class M002RegisterParentActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.m002_activity)

        val viewPager: ViewPager2 = findViewById(R.id.pager)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "検索"
                1 -> tab.text = "手入力"
            }
        }.attach()
    }

    override fun onStart() {
        super.onStart()

        setActionBar("登録")
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> M003ResearchFragment()
                1 -> M004ManualInputFragment()
                else -> throw IllegalArgumentException("Invalid position")
            }
        }
    }

    companion object {
        private const val NUM_PAGES = 2
    }
}
