package com.kobyakov.githubrepos.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import android.widget.TextView;

import com.kobyakov.githubrepos.R;
import com.kobyakov.githubrepos.model.Repository;
import com.kobyakov.githubrepos.utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RepositoryDetailsActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();
    public final int LAYOUT = R.layout.activity_repository_details;
    public final String REPOSITORY = "repository";

    @BindView(R.id.repository_name)
    TextView repositoryName;
    @BindView(R.id.repository_desc)
    TextView repositoryDescription;
    @BindView(R.id.repository_link)
    TextView repositoryLink;
    @BindView(R.id.repository_create_at)
    TextView repositoryCreateAt;
    @BindView(R.id.repository_update_at)
    TextView repositoryUpdateAt;
    @BindView(R.id.repository_score)
    TextView repositoryScore;

    Repository repository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        initData();

        String description = repository.getDescription() != null ? repository.getDescription() : getString(R.string.no_desc);
        String url = repository.getHtmlUrl();
        String createDate = Util.getStrDateByCustomPattern(repository.getCreatedAt());
        String updateDate = Util.getStrDateByCustomPattern(repository.getUpdatedAt());
        long score = repository.getStargazersCount();

        repositoryName.setText(repository.getName());
        repositoryDescription.setText(description);
        repositoryLink.setText(getTextWithHTMLFormat(R.string.repository_link, url));
        repositoryCreateAt.setText(getTextWithHTMLFormat(R.string.created_at, createDate));
        repositoryUpdateAt.setText(getTextWithHTMLFormat(R.string.update_at, updateDate));
        repositoryScore.setText(getTextWithHTMLFormat(R.string.score, score));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData() {
        ButterKnife.bind(this);
        if (getIntent().getExtras() != null) {
            repository = getIntent().getExtras().getParcelable(REPOSITORY);
        }
    }

    public Spanned getTextWithHTMLFormat(int idStringResource, String text) {
        String stringWithoutFormatHtml = getString(idStringResource, text);

        return Html.fromHtml(stringWithoutFormatHtml);
    }

    public Spanned getTextWithHTMLFormat(int idStringResource, long value) {
        String stringWithoutFormatHtml = getString(idStringResource, value);

        return Html.fromHtml(stringWithoutFormatHtml);
    }
}