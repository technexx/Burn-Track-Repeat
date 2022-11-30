package burn.track.repeat.SettingsFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import burn.track.repeat.R;


public class DisclaimerFragment extends Fragment {



    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.disclaimer_fragment_layout, container, false);
        root.setBackgroundColor(getResources().getColor(R.color.alien_black));

        return root;

    }
}
