package com.kobyakov.githubrepos.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.kobyakov.githubrepos.R;
import com.kobyakov.githubrepos.RecyclerTouchListener;
import com.kobyakov.githubrepos.adapter.RepositoryAdapter;
import com.kobyakov.githubrepos.model.APIError;
import com.kobyakov.githubrepos.model.Repository;
import com.kobyakov.githubrepos.model.User;
import com.kobyakov.githubrepos.utils.RVEmptyObserver;
import com.kobyakov.githubrepos.viewmodel.UserInfoViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.LinearLayout.VERTICAL;

public class UserInfoActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public final String TAG = getClass().getSimpleName();
    public final String REPOSITORY = "repository";
    public static final int LAYOUT = R.layout.activity_user_info;

    public static final int DELAY = 2000;
    public static final int UPLOADING_COUNT = 10;

    private RepositoryAdapter adapter;
    public UserInfoViewModel viewModel;

    List<Repository> allRepositories;
    List<Repository> repositoriesForAdapter;

    @BindView(R.id.recycler_view_repository)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.owner_repository_avatar)
    ImageView ownerRepositoryAvatar;
    @BindView(R.id.owner_repository_name)
    TextView ownerRepositoryName;
    @BindView(R.id.owner_repository_login)
    TextView ownerRepositoryLogin;
    @BindView(R.id.error_message)
    TextView errorMessage;
    @BindView(R.id.request_text)
    TextView requestText;
    @BindView(R.id.content_block)
    ConstraintLayout contentBlock;
    @BindView(R.id.error_block)
    ConstraintLayout errorBlock;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;

    RequestManager glide;
    SearchView searchView;
    EditText edittext;

    private String userNameGithub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        initData();

        viewModel.getApiErrorLiveData().observe(this, error -> {
            if (error != null) {
                swipeRefreshLayout.setRefreshing(false);
                fillErrorBlock(error);
            }
        });

        viewModel.getUserAndRepositoryHolderLive().observe(this, userWithRepositories -> {
            if (userWithRepositories != null) {
                swipeRefreshLayout.setRefreshing(false);

                User userInfo = userWithRepositories.getUser();
                List<Repository> repositories = userWithRepositories.getRepositories();

                fillUserInfoBlock(userInfo);
                transferRepositoriesToAdapter(repositories);

                progressBar.setVisibility(View.GONE);
                contentBlock.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        handlingDataFromOtherApps();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);

        searchView = (SearchView) item.getActionView();
        searchView.setQueryHint(getString(R.string.enter_github_username));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                userNameGithub = s;
                progressBar.setVisibility(View.VISIBLE);
                viewModel.searchRepositories(s);
                adapter.setLoading(false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRefresh() {
        viewModel.searchRepositories(userNameGithub);
        adapter.setLoading(false);
    }

    private void initData() {
        ButterKnife.bind(this);
        setAdapterAndRecyclerView();
        glide = Glide.with(this);
        viewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void fillUserInfoBlock(User userInfo) {
        String urlAvatar = userInfo.getAvatarUrl();
        String username = userInfo.getName() != null ? userInfo.getName() : getString(R.string.unknown);
        String login = userInfo.getLogin();

        glide.load(urlAvatar)
                .error(glide.load(R.drawable.help).apply(RequestOptions.circleCropTransform()))
                .apply(RequestOptions.circleCropTransform())
                .into(ownerRepositoryAvatar);

        ownerRepositoryName.setText(Html.fromHtml(getString(R.string.full_name, username)));
        ownerRepositoryLogin.setText(Html.fromHtml(getString(R.string.login, login)));
    }

    private void transferRepositoriesToAdapter(List<Repository> repositories) {
        if (repositories != null && repositories.size() > 0) {
            allRepositories = repositories;

            if (repositories.size() > 10) {
                repositoriesForAdapter = new ArrayList<>(repositories.subList(0, 10));
            } else {
                repositoriesForAdapter = new ArrayList<>(repositories.subList(0, repositories.size()));
            }

            adapter.setData(repositoriesForAdapter);
        } else {
            adapter.setData(null);
        }
    }

    private void workWithDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        edittext = new EditText(this);
        builder.setView(edittext);
        builder.setTitle(R.string.enter_github_username);

        builder.setPositiveButton(R.string.btn_ok, null);
        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                String userInput = edittext.getText().toString().trim();
                if (userInput.length() == 0) {
                    Toast.makeText(UserInfoActivity.this, R.string.edit_text_error, Toast.LENGTH_LONG).show();
                } else {
                    dialogInterface.dismiss();

                    searchView.setQuery(userInput, true);
                    searchView.setFocusable(true);
                    searchView.setIconified(false);
                    searchView.clearFocus();

                    progressBar.setVisibility(View.VISIBLE);
                }
            });
        });

        dialog.show();
    }

    public void setAdapterAndRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RepositoryAdapter(recyclerView, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, VERTICAL));
        recyclerView.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RVEmptyObserver(recyclerView, emptyLayout, recyclerView));

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, (view, position) -> {
            Repository selectedRepository = allRepositories.get(position);
            goToRepositoryDetailsActivity(selectedRepository);
        }));

        adapter.setLoadMore(() -> {
            if (repositoriesForAdapter.size() < allRepositories.size()) {
                repositoriesForAdapter.add(null);
                recyclerView.post(() -> adapter.notifyItemInserted(repositoriesForAdapter.size() - 1));

                new Handler().postDelayed(() -> {
                    repositoriesForAdapter.remove(repositoriesForAdapter.size() - 1);
                    adapter.notifyItemRemoved(repositoriesForAdapter.size());

                    int index = repositoriesForAdapter.size();
                    int end = index + UPLOADING_COUNT;
                    if ((allRepositories.size() - end) >= 0) {
                        repositoriesForAdapter.addAll(allRepositories.subList(index, end));
                    } else {
                        repositoriesForAdapter.addAll(allRepositories.subList(index, allRepositories.size()));
                    }

                    adapter.notifyDataSetChanged();
                    adapter.setLoaded();
                }, DELAY);
            } else {
                Snackbar.make(recyclerView, R.string.all_repo_loaded, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void fillErrorBlock(APIError apiError) {
        errorMessage.setText(Html.fromHtml(getString(R.string.result_text, apiError.getMessage())));
        requestText.setText(Html.fromHtml(getString(R.string.request_text, apiError.getRequest())));
        progressBar.setVisibility(View.GONE);
        contentBlock.setVisibility(View.GONE);
        errorBlock.setVisibility(View.VISIBLE);
    }

    private void handlingDataFromOtherApps() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String receivedText = intent.getStringExtra(Intent.EXTRA_TEXT);

                searchView.setQuery(receivedText, true);
                searchView.setFocusable(true);
                searchView.setIconified(false);
                searchView.clearFocus();
                progressBar.setVisibility(View.VISIBLE);
            }
        } else {
            workWithDialog();
        }
    }

    private void goToRepositoryDetailsActivity(Repository repository) {
        Intent intent = new Intent(this, RepositoryDetailsActivity.class);
        intent.putExtra(REPOSITORY, repository);
        startActivity(intent);
    }
}