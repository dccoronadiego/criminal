package corona.diego.criminal

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import corona.diego.criminal.databinding.FragmentCrimeListBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import java.util.*

private const val TAG = "CrimeListFragment"
        private var _binding: FragmentCrimeListBinding? = null

val binding
    get() = checkNotNull(_binding) {
        "Cannot access binding because it is null. Is the view visible?"
    }

    class CrimeListFragment : Fragment() {
    private val crimeListViewModel: CrimeListViewModel by viewModels()
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setHasOptionsMenu(true)
        }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCrimeListBinding.inflate(inflater, container, false)
        binding.crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimeListViewModel.crimes.collect { crimes ->
                    binding.crimeRecyclerView.adapter =
                    CrimeListAdapter(crimes) { crimeId ->
                    findNavController().navigate(
                        CrimeListFragmentDirections.showCrimeDetail(crimeId)
                    )
                    }
            }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
        override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
            super.onCreateOptionsMenu(menu, inflater)
            inflater.inflate(R.menu.fragment_crime_list, menu)
        }
        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.new_crime -> {
                    showNewCrime()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
        private fun showNewCrime() {
            viewLifecycleOwner.lifecycleScope.launch {
                val newCrime = Crime(
                    id = UUID.randomUUID(),
                    title = "",
                    date = Date(),
                    isSolved = false
                )
                crimeListViewModel.addCrime(newCrime)
                findNavController().navigate(
                    CrimeListFragmentDirections.showCrimeDetail(newCrime.id)
                )
            }
        }
    }