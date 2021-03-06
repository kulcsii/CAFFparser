import { faCheck } from '@fortawesome/fontawesome-free-solid';
import {
  faEdit,
  faTrashAlt,
  IconDefinition,
} from '@fortawesome/free-regular-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useState } from 'react';
import { CommentService } from '../services/openapi';

interface CommentCardProps {
  content: string;
  id: number;
  modifiable: boolean;
  username: string;
  imageId: number;
  deleteComment: Function;
}

const CommentCard = (props: CommentCardProps) => {
  const faIcon = faCheck as IconDefinition;

  const [editing, setEditing] = useState(false);
  const [editText, setEditText] = useState(props.content);

  const handleDelete = (commentId: number) => {
    props.deleteComment(commentId);
  };

  const handleEdit = () => {
    setEditing(true);
  };

  const handleConfirm = () => {
    CommentService.updateComment(props.id, {
      content: editText,
      imageId: props.imageId,
    })
      .then((res) => {
        setEditText(res.content!);
      })
      .catch((err) => {
        console.log('Can not update comment');
      })
      .finally(() => {
        setEditing(false);
      });
  };

  return (
    <div style={styles.commentCard}>
      <div style={styles.commentData}>
        <div style={styles.username}>{props.username}</div>
        <div>
          {props.modifiable && (
            <div>
              {!editing && (
                <span className="btn btn-warning">
                  <FontAwesomeIcon
                    onClick={() => handleEdit()}
                    icon={faEdit}
                  ></FontAwesomeIcon>
                </span>
              )}
              {editing && (
                <span
                  className="btn btn-success"
                  onClick={() => handleConfirm()}
                >
                  <FontAwesomeIcon icon={faIcon}></FontAwesomeIcon>
                </span>
              )}{' '}
              <span
                className="btn btn-danger"
                onClick={() => handleDelete(props.id)}
              >
                <FontAwesomeIcon icon={faTrashAlt}></FontAwesomeIcon>
              </span>
            </div>
          )}
        </div>
      </div>
      {!editing && <p>{editText}</p>}
      {editing && (
        <input
          style={styles.input}
          className="form-control"
          type={'text'}
          value={editText}
          onChange={(e) => {
            setEditText(e.target.value);
          }}
        />
      )}
    </div>
  );
};

const styles = {
  commentCard: {
    boxShadow: '0 2px 1px 1px rgba(0, 0, 0, 0.1)',
    borderRadius: '5px',
    padding: '20px',
    marginBottom: 15,
  },
  commentData: {
    display: 'flex',
    justifyContent: 'space-between',
  },
  username: {
    color: 'grey',
  },
  input: {
    width: '80%',
  },
};

export default CommentCard;
