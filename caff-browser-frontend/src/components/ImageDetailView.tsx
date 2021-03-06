import { useEffect, useState } from 'react';
import { Badge, Button } from 'react-bootstrap';
import { useParams } from 'react-router-dom';
import { CommentService, ImageDto, ImageService } from '../services/openapi';
import StaticService from '../services/StaticService';
import CommentCard from './CommentCard';

const ImageDetailView = () => {
  const [image, setImage] = useState<ImageDto>();
  const [commentText, setCommentText] = useState('');
  const { id } = useParams();

  useEffect(() => {
    ImageService.getImage(+id!)
      .then((img) => {
        setImage(img);
      })
      .catch((err) => console.error('Failed to fetch image!'));
  }, [id]);

  const handleDeleteComment = (commentId: number) => {
    CommentService.deleteComment(commentId)
      .then((res) => {
        ImageService.getImage(+id!)
          .then((img) => {
            setImage(img);
          })
          .catch((error) => {
            console.error(error);
          });
      })
      .catch((err) => {
        console.log('Can not delete comment');
      });
  };

  const handleAddComment = () => {
    CommentService.createComment({
      imageId: image?.id,
      content: commentText,
    })
      .then((res) => {
        ImageService.getImage(+id!)
          .then((img) => {
            setImage(img);
          })
          .catch((error) => {
            console.error(error);
          });
      })
      .catch((error) => {
        console.error(error);
      });
  };

  const renderBadges = () => {
    return image?.tags?.map((tag, idx) => {
      return (
        <>
          <Badge key={idx} className="hover-red" bg="secondary">
            {tag}
          </Badge>{' '}
        </>
      );
    });
  };

  return (
    <div className="row">
      <div className="col-6 mb-5 mt-2">
        <img
          style={styles.mainImage}
          src={StaticService.getImage(image?.gifPath!)}
          alt=""
        />
      </div>
      <div className="col-6 mx-auto my-3">
        <h2 className="mb-4">
          <b>{image?.userDisplayName}</b>
        </h2>
        <div className="row">
          <div className="col-4">
            <p>Size:</p>
          </div>
          <div className="col-8">
            <p>
              {image?.width} x {image?.height}
            </p>
          </div>
        </div>
        <div className="row">
          <div className="col-4">
            <p>Created by:</p>
          </div>
          <div className="col-8">
            <p>{image?.credit}</p>
          </div>
        </div>
        <div className="row">
          <div className="col-4">
            <p>On date:</p>
          </div>
          <div className="col-8">
            <p>
              {image
                ?.date!.replace('T', ' ')
                .substring(0, image?.date!.length - 3)}
            </p>
          </div>
        </div>
        <div className="row">
          <div className="col-4">
            <p>Uploaded by:</p>
          </div>
          <div className="col-8">
            <p>{image?.userDisplayName}</p>
          </div>
        </div>
        {renderBadges()}
      </div>
      <div style={styles.commentsSection}>
        {image?.commentable && (
          <div className="input-group my-3">
            <input
              className="form-control"
              placeholder="Add new comment..."
              type={'text'}
              value={commentText}
              onChange={(e) => {
                setCommentText(e.target.value);
              }}
            />
            <div className="input-group-append">
              <Button onClick={() => handleAddComment()} variant="primary">
                Submit
              </Button>
            </div>
          </div>
        )}

        {image?.comments?.map((comment, idx) => (
          <CommentCard
            key={idx}
            content={comment.content!}
            id={comment.id!}
            modifiable={comment.modifiable!}
            username={comment.userDisplayName!}
            imageId={comment.imageId!}
            deleteComment={handleDeleteComment}
          />
        ))}
      </div>
    </div>
  );
};

const styles = {
  mainImage: {
    display: 'block',
    margin: 'auto',
    maxWidth: 400,
    maxHeight: 400,
  },
  infoBar: {
    maxWidth: 400,
    height: 40,
  },
  commentsSection: {
    display: 'block',
    margin: 'auto',
  },
};

export default ImageDetailView;
